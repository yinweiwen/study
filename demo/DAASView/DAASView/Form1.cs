using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace DAASView
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            dgv.DataBindingComplete += dgv_DataBindingComplete;
            dgv.CellContentClick += dgv_CellContentClick;
        }

        void dgv_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (dgv.Columns[e.ColumnIndex].Name == "data")
            {
                var data = dgv[e.ColumnIndex, e.RowIndex].Value as double[];
                if (data != null)
                {
                    var str = string.Join("\n", data);
                    new TextWin(str).Show(this);
                }
            }
        }

        void dgv_DataBindingComplete(object sender, DataGridViewBindingCompleteEventArgs e)
        {
            dgv.Columns["max"].HeaderText = @"max 最大";
            dgv.Columns["pp"].HeaderText = @"pp 峰峰";
            foreach (DataGridViewRow r in dgv.Rows)
            {
                r.Cells["data"] = new DataGridViewButtonCell();
            }
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (folderBrowserDialog1.ShowDialog(this) == DialogResult.OK)
            {
                var path = folderBrowserDialog1.SelectedPath;
                LoadPath(path);
            }
        }

        private string fileType = "cloudVib"; // dat/odb
        private string CheckFileType(FileInfo fi)
        {
            try
            {
                CloudVibFileTitle title;
                double[] org;
                string err;
                if (!ReadCloudVibFile(fi.FullName, out title, out org, out err))
                {
                    using (var sr = new StreamReader(fi.FullName))
                    {
                        var line = sr.ReadLine();
                        if (!string.IsNullOrEmpty(line))
                        {
                            double d;
                            if (double.TryParse(line, out d))
                            {
                                return "dat";
                            }
                        }
                    }
                }
                else
                {
                    return "cloud_vib";
                }
            }
            catch (Exception)
            {
                // ignored
            }
            return "";
        }

        private void LoadPath(string p)
        {
            var dir = new DirectoryInfo(p);
            var files = dir.GetFiles("*.*");
            var tb = new DataTable();
            tb.Columns.Add("file");
            tb.Columns.Add("module");
            tb.Columns.Add("ch");
            tb.Columns.Add("count", typeof(long)); // 点数
            tb.Columns.Add("max", typeof(double));
            tb.Columns.Add("pp", typeof(double));
            tb.Columns.Add("time");
            tb.Columns.Add("data", typeof(object));
            if (files.Any())
            {
                fileType = CheckFileType(files.First());
            }
            foreach (var fi in files)
            {
                try
                {
                    switch (fileType)
                    {
                        case "cloudVib":
                            {
                                CloudVibFileTitle title;
                                double[] org;
                                string err;
                                if (!ReadCloudVibFile(fi.FullName, out title, out org, out err))
                                {
                                    continue;
                                }
                                var ch = title.CHNum;
                                var mod = title.DeviceID.ToString();
                                var time = new DateTime(2000 + title.year, title.mon, title.day, title.hour, title.min, title.sec);
                                var pp = org.Max() - org.Min(); // 峰峰
                                var max = org.OrderBy(Math.Abs).Last();

                                var nr = tb.NewRow();
                                nr["file"] = fi.Name;
                                nr["module"] = mod;
                                nr["ch"] = ch;
                                nr["count"] = org.Length;
                                nr["max"] = max;
                                nr["pp"] = pp;
                                nr["time"] = time;
                                nr["data"] = org;
                                tb.Rows.Add(nr);
                            }
                            break;
                        case "dat":
                            {
                                string mod;
                                int ch;
                                DateTime time;
                                double[] org;
                                ParseVibDat(fi.FullName, out mod, out ch, out time, out org);
                                var pp = org.Max() - org.Min(); // 峰峰
                                var max = org.OrderBy(Math.Abs).Last();

                                var nr = tb.NewRow();
                                nr["file"] = fi.Name;
                                nr["module"] = mod;
                                nr["ch"] = ch;
                                nr["count"] = org.Length;
                                nr["max"] = max;
                                nr["pp"] = pp;
                                nr["time"] = time;
                                nr["data"] = org;
                                tb.Rows.Add(nr);
                        }
                            break;
                    }
                }
                catch (Exception)
                {
                    // ignored
                }
            }

            dgv.DataSource = tb;
            dgv.Update();
        }

        public static void ParseVibDat(string f, out string mod, out int ch, out DateTime time, out double[] datas)
        {
            //10587_1_20210129000005436.dat
            var sps = Path.GetFileNameWithoutExtension(f).Split('_');
            mod = sps[0];
            ch = int.Parse(sps[1]);
            time = DateTime.ParseExact(sps[2], new[] { "yyyy-MM-dd HH:mm:ss.fff", "yyyyMMddHHmmssfff", "yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss" }, null, DateTimeStyles.None);
            var res = new List<double>();
            using (var sr = new StreamReader(f))
            {
                var line = sr.ReadLine();
                while (!string.IsNullOrEmpty(line))
                {
                    try
                    {
                        res.Add(double.Parse(line));
                    }
                    catch (Exception)
                    {
                        break;
                    }
                    line = sr.ReadLine();
                }
            }
            datas = res.ToArray();
        }

        public static bool ParseCloudVib(string path)
        {
            CloudVibFileTitle title;
            double[] org;
            string err;
            if (!ReadCloudVibFile(path, out title, out org, out err))
            {
                return true;
            }
            var ch = title.CHNum;
            var mod = title.DeviceID.ToString();
            var time = new DateTime(2000 + title.year, title.mon, title.day, title.hour, title.min, title.sec);
            return false;
        }


        /// <summary>
        /// 解析云振动数据文件
        /// </summary>
        /// <param name="file">文件</param>
        /// <param name="title">out 振动头信息</param>
        /// <param name="data">out 振动数据</param>
        /// <param name="err">out 错误信息</param>
        /// <returns></returns>
        private static bool ReadCloudVibFile(string file, out CloudVibFileTitle title, out double[] data, out string err)
        {
            try
            {
                err = string.Empty;
                using (var fs = new FileStream(file, FileMode.Open, FileAccess.Read))
                {
                    var br = new BinaryReader(fs);
                    var t = br.ReadUInt16();
                    var l = br.ReadUInt16();
                    title = (CloudVibFileTitle)StructConvert.BytesToStruct(br.ReadBytes(l), typeof(CloudVibFileTitle));
                    var cnt = title.L_Date / 4;
                    data = new double[cnt];
                    for (var i = 0; i < cnt; i++)
                    {
                        data[i] = br.ReadSingle();
                    }
                    fs.Close();
                }
                return true;
            }
            catch (Exception ex)
            {
                title = new CloudVibFileTitle();
                data = null;
                err = ex.Message;
                return false;
            }
        }
    }


    /// <summary>
    /// 云网关振动数据格式
    /// </summary>
    public struct CloudVibFileTitle
    {
        public byte diVer; //版本号
        public byte CHNum; //通道号
        public ushort DeviceID; //设备ID
        public float SampleFreq; //采样频率
        public float FilterFreq; // 滤波频率
        public byte GainAmplifier; //放大倍数
        public byte TriggerType; //采样方式
        public byte year; // 采集时刻:年
        public byte mon; // 采集时刻:月
        public byte day; // 采集时刻:日
        public byte hour; // 采集时刻:时
        public byte min; // 采集时刻:分
        public byte sec; // 采集时刻:秒
        public uint Reserved1;
        public uint Reserved2;
        public ushort Reserved3;
        public ushort T_Data;
        public uint L_Date; // 数据区数据长度
    }
}
