using log4net;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace FtpSync
{
    /**
     * 
     * subdir: 无法保留本地目录结构
     * 
     **/
    class Program
    {
        static ILog log = LogManager.GetLogger("main");

        static int intervalSecs = int.Parse(ConfigurationManager.AppSettings["intervalSecs"] ?? "30");

        static string localDir = ConfigurationManager.AppSettings["localDir"];

        static bool scanSubs = bool.Parse(ConfigurationManager.AppSettings["scanSubs"] ?? "false");

        static string filter = ConfigurationManager.AppSettings["filter"] ?? "*.*";

        static bool ignore_system = bool.Parse(ConfigurationManager.AppSettings["ignore_system"] ?? "true");
        static bool ignore_hidden = bool.Parse(ConfigurationManager.AppSettings["ignore_hidden"] ?? "true");

        static string remoteDir = ConfigurationManager.AppSettings["remoteDir"] ?? "";
        static string remoteDateDir = ConfigurationManager.AppSettings["RemoteDateDir"] ?? "yyyyMMdd";

        static int threadCount = int.Parse(ConfigurationManager.AppSettings["threadCount"] ?? "3");

        static string backupDir = ConfigurationManager.AppSettings["backupDir"];
        static string backupErrDir = ConfigurationManager.AppSettings["backupErrDir"] ?? backupDir;

        static int retryLimit = int.Parse(ConfigurationManager.AppSettings["retryLimit"] ?? "3");
        static int retryInterval = int.Parse(ConfigurationManager.AppSettings["retryInterval"] ?? "10");

        static void Main(string[] args)
        {
            log.InfoFormat("ftp: server {0} user {1} passwords {2}\n", FTPHelper.FtpServerIP, FTPHelper.FtpUserID, FTPHelper.FtpPassword);

            log.InfoFormat("scan interval {0} seconds", intervalSecs);

            log.Info("service started");

            CheckLocal(backupDir);
            CheckLocal(backupErrDir);

            while (true)
            {
                try
                {
                    CheckDir(remoteDir);
                    var dirInfo = new DirectoryInfo(localDir);
                    var sopt = scanSubs ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly;
                    var files = dirInfo.GetFiles(filter, sopt);
                    log.InfoFormat("tick {0}", files.Length);
                    Parallel.ForEach(files, new ParallelOptions { MaxDegreeOfParallelism = threadCount }, file => Process(file));
                }
                catch (Exception ex)
                {
                    log.Warn("some problem:", ex);
                }
                finally
                {
                    Thread.Sleep(intervalSecs * 1000);
                }
            }
        }

        static void Process(FileInfo file)
        {
            if (ignore_system && file.Attributes.HasFlag(FileAttributes.System)
                || ignore_hidden && file.Attributes.HasFlag(FileAttributes.Hidden)) return;
            bool res = false;
            try
            {
                var rd = string.IsNullOrEmpty(remoteDateDir) ? remoteDir : Path.Combine(remoteDir, file.CreationTime.ToString(remoteDateDir));
                res = UploadRetry(file.FullName, rd, null);
                log.InfoFormat("upload {1}:{0}", file.FullName, res ? "success" : "failed");
            }
            catch (Exception e)
            {
                log.WarnFormat("file upload failed: {0} {1}", file.Name, e);
            }
            var back = res ? backupDir : backupErrDir;
            try
            {
                if (string.IsNullOrEmpty(back)) file.Delete();
                else
                {
                    try
                    {
                        file.MoveTo(Path.Combine(back, file.Name));
                    }
                    catch
                    {
                        log.Warn("have problem when move file, so delete:" + file.FullName);
                        file.Delete();
                    }
                }
            }
            catch (IOException)
            {
                log.Warn("have problem when delete file:" + file.FullName);
            }
        }

        static void CheckDir(string dir = null)
        {
            string rd = null;
            try
            {
                rd = dir ?? (string.IsNullOrEmpty(remoteDateDir) ? remoteDir : Path.Combine(remoteDir, DateTime.Now.ToString(remoteDateDir)));
                FTPHelper.MakeDir(rd);
            }
            catch (WebException ex)
            {
                FtpWebResponse response = (FtpWebResponse)ex.Response;
                if (response.StatusCode == FtpStatusCode.ActionNotTakenFileUnavailable)
                {
                    // 文件不可用：目录已创建 或 无法/权限创建
                }
                else
                {
                }
                response.Close();
            }
        }

        static void CheckLocal(string dir)
        {
            if (string.IsNullOrEmpty(dir)) return;
            var di = new DirectoryInfo(dir);
            if (!di.Exists) di.Create();
        }

        static bool UploadRetry(string localFullPath, string remoteFilepath, Action<int, int> updateProgress = null, int c = 0)
        {
            bool res = false;
            var unavalilable = false;
            try
            {
                res = FTPHelper.FtpUploadBroken(localFullPath, remoteFilepath, updateProgress);
            }
            catch (WebException ex)
            {
                FtpWebResponse response = (FtpWebResponse)ex.Response;
                if (response.StatusCode == FtpStatusCode.ActionNotTakenFileUnavailable)
                {
                    // 文件不可用，可能需要创建文件夹
                    CheckDir(remoteFilepath);
                    unavalilable = true;
                }
                response.Close();
            }
            c++;
            if (res || c >= retryLimit) return res;
            else
            {
                if (!unavalilable)
                    Thread.Sleep(retryInterval * 1000);
                return UploadRetry(localFullPath, remoteFilepath, updateProgress, c);
            }
        }

    }
}
