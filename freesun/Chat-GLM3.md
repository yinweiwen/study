Chat-GLM3

> 环境已运行 [Langchain-Chatchat](https://github.com/chatchat-space/Langchain-Chatchat)
>
> `fs-ai/freesun123` @ 10.8.30.86
> 发布站点：http://10.8.30.86:8501/
>
> 启动命令：
>
> ```sh
> fs-ai@FS-AI-Training:~/chatchat/Langchain-Chatchat$ conda env list
> # conda environments:
> #
> base                     /home/fs-ai/miniconda3
> chatchat              *  /home/fs-ai/miniconda3/envs/chatchat
> chatglm3-demo            /home/fs-ai/miniconda3/envs/chatglm3-demo
> llama_factory            /home/fs-ai/miniconda3/envs/llama_factory
> 
> (chatchat) fs-ai@FS-AI-Training:~/chatchat/Langchain-Chatchat$ conda activate chatchat
> (chatchat) fs-ai@FS-AI-Training:~/chatchat/Langchain-Chatchat$ python startup.py -a
> ```



查看`Navidia` CUDA版本，并根据官方安装[PyTorch](https://pytorch.org/get-started/locally/)。

```sh
(chatglm3-demo) fs-ai@FS-AI-Training:~/demo/ChatGLM3/composite_demo$ nvcc --version
nvcc: NVIDIA (R) Cuda compiler driver
Copyright (c) 2005-2023 NVIDIA Corporation
Built on Fri_Sep__8_19:17:24_PDT_2023
Cuda compilation tools, release 12.3, V12.3.52
Build cuda_12.3.r12.3/compiler.33281558_0
```



启动composite-demo后报错：

1. no module name '`huggingface_hub`'

   `pip install "huggingface_hub<0.22.0"` (其他类似的库一样，需要手动安装)




2. `WARNING:root:Some parameters are on the meta device device because they were offloaded to the cpu`.

	   貌似是显存不够了，之前运行的‘`Langchain-Chatchat$`’没有停

   ```sh
   (chatglm3-demo) fs-ai@FS-AI-Training:~/demo/ChatGLM3/composite_demo$ nvidia-smi
   Mon Apr  8 15:33:45 2024
   +---------------------------------------------------------------------------------------+
   | NVIDIA-SMI 545.29.02              Driver Version: 545.29.02    CUDA Version: 12.3     |
   |-----------------------------------------+----------------------+----------------------+
   | GPU  Name                 Persistence-M | Bus-Id        Disp.A | Volatile Uncorr. ECC |
   | Fan  Temp   Perf          Pwr:Usage/Cap |         Memory-Usage | GPU-Util  Compute M. |
   |                                         |                      |               MIG M. |
   |=========================================+======================+======================|
   |   0  NVIDIA GeForce RTX 4060 Ti     On  | 00000000:01:00.0 Off |                  N/A |
   |  0%   39C    P8               5W / 165W |  14006MiB / 16380MiB |      0%      Default |
   |                                         |                      |                  N/A |
   +-----------------------------------------+----------------------+----------------------+
   
   +---------------------------------------------------------------------------------------+
   | Processes:                                                                            |
   |  GPU   GI   CI        PID   Type   Process name                            GPU Memory |
   |        ID   ID                                                             Usage      |
   |=======================================================================================|
   |    0   N/A  N/A      1060      G   /usr/lib/xorg/Xorg                          107MiB |
   |    0   N/A  N/A      1299      G   /usr/bin/gnome-shell                         11MiB |
   |    0   N/A  N/A    138894      C   ...miniconda3/envs/chatchat/bin/python     1448MiB |
   |    0   N/A  N/A    512811      C   ...miniconda3/envs/chatchat/bin/python    12424MiB |
   +---------------------------------------------------------------------------------------+
   
   ```


3. 

模型下载：

```sh
$ git lfs install
$ git clone https://huggingface.co/THUDM/chatglm3-6b
$ git clone https://huggingface.co/BAAI/bge-large-zh
```

