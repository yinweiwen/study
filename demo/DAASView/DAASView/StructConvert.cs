using System;
using System.Runtime.InteropServices;

namespace DAASView
{
    /// <summary>
    /// 结构体<->字节数组转换
    /// </summary>
    public class StructConvert
    {
        public static Byte[] StructToBytes(Object structure)
        {
            Int32 size = Marshal.SizeOf(structure);
            Console.WriteLine(size);
            IntPtr buffer = Marshal.AllocHGlobal(size);
            try
            {
                Marshal.StructureToPtr(structure, buffer, false);
                Byte[] bytes = new Byte[size];
                Marshal.Copy(buffer, bytes, 0, size);
                return bytes;
            }
            finally
            {
                Marshal.FreeHGlobal(buffer);
            }
        }

        public static Object BytesToStruct(Byte[] bytes, Type strcutType)
        {
            Int32 size = Marshal.SizeOf(strcutType);
            IntPtr buffer = Marshal.AllocHGlobal(size);
            try
            {
                Marshal.Copy(bytes, 0, buffer, size);
                return Marshal.PtrToStructure(buffer, strcutType);
            }
            finally
            {
                Marshal.FreeHGlobal(buffer);
            }
        }
    }
}
