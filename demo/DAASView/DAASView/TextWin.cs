using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace DAASView
{
    public partial class TextWin : Form
    {
        public TextWin(string s)
        {
            InitializeComponent();
            richTextBox1.Text = s;
        }
    }
}
