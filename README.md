## SDM - Lab 2 @ UPC 👨🏻‍💻

</br>

<div>
  <a href="https://open.vscode.dev/mohammadzainabbas/SDM-Lab-2" target="_blank" style="cursor: pointer;"> 
    <img src="https://open.vscode.dev/badges/open-in-vscode.svg" style="cursor: pointer;"/>
  </a>
</div>

</br>

### Table of contents

- [Introduction](#introduction)
  * [GraphX](#graphx)
  * [Pregel](#pregel)
- [Setup](#setup)
  * [Mac](#mac)
  * [Linux](#linux)
- [Run](#run)
  * [VS Code](#vscode)
  * [Terminal](#terminal)
---

<a id="introduction" />

#### 1. Introduction

__`Data drives the world.`__ In this big data era, the need to analyse large volumes of data has become ever more challenging and quite complex. Several different eco-systems have been developed which try to solve some particular problem. One of the main tool in Big Data eco system is the [_Apache Spark_](https://spark.apache.org/)


__Apache Spark__ analysis of big data became essential easier. Spark brings a lot implementation of useful algorithms for data mining, data analysis, machine learning, algorithms on graphs. Spark takes on the challenge of implementing sophisticated algorithms with tricky optimization and ability to run your code on distributed cluster. Spark effectively solve problems like fault tolerance and provide simple API to make the parallel computation.

<a id="graphx" />

##### 1.1. GraphX

[GraphX](https://spark.apache.org/docs/latest/graphx-programming-guide.html) is a new component in Spark for graphs and graph-parallel computation. At a high level, GraphX extends the Spark RDD by introducing a new Graph abstraction: a directed multigraph with properties attached to each vertex and edge.


This repository serves as a starting point for working with _Spark GraphX API_. As part of our SDM lab, we'd be focusing on getting a basic idea about how to work with `pregel` and get a hands-on experience with distributed processing of large graph.

<a id="pregel" />

##### 1.2. Pregel

Pregel, originally developed by Google, is essentially a message-passing interface which facilitates the processing of large-scale graphs. _Apache Spark's GraphX_ module provides the [Pregel API](https://spark.apache.org/docs/latest/graphx-programming-guide.html#pregel-api) which allow us to write distributed graph programs / algorithms. For more details, kindly check out the [original paper](https://github.com/mohammadzainabbas/SDM-Lab-2/blob/main/docs/pregel.pdf)

---

<a id="setup" />

#### 2. Setup


Before starting, you may need to setup your machine first. Please follow the below mentioned guides to setup Spark and Maven on your machine.

<a id="mac" />

##### 2.1. Mac

We have created a setup script which will setup brew, apache-spark, maven and conda enviornment. If you are on Mac machine, you can run the following commands:

```bash
git clone https://github.com/mohammadzainabbas/SDM-Lab-2.git
cd SDM-Lab-2 && sh scripts/setup.sh
```

<a id="linux" />

##### 2.2. Linux

If you are on Linux, you need to install [Apache Spark](https://spark.apache.org) by yourself. You can follow this [helpful guide](https://computingforgeeks.com/how-to-install-apache-spark-on-ubuntu-debian/) to install `apache spark`. You can install maven via this [guide](https://maven.apache.org/install.html).

We also recommend you to install _conda_ on your machine. You can setup conda from [here](https://docs.conda.io/projects/conda/en/latest/user-guide/install/linux.html)

After you have conda, create new enviornment via:

```bash
conda create -n spark_env python=3.8
```

> Note: We are using Python3.8 because spark doesn't support Python3.9 and above (at the time of writing this)

Activate your enviornment:

```bash
conda activate spark_env
```

Now, you need to install _pyspark_:

```bash
pip install pyspark
```

If you are using bash:

```bash

echo "export PYSPARK_DRIVER_PYTHON=$(which python)" >> ~/.bashrc
echo "export PYSPARK_DRIVER_PYTHON_OPTS=''" >> ~/.bashrc
. ~/.bashrc

```

And if you are using zsh:

```zsh

echo "export PYSPARK_DRIVER_PYTHON=$(which python)" >> ~/.zshrc
echo "export PYSPARK_DRIVER_PYTHON_OPTS=''" >> ~/.zshrc
. ~/.zshrc

```

---

<a id="run" />

#### 3. Run

Since, this is a typical maven project, you can run it however you'd like to run a maven project. To facilitate you, we provide you two ways to run this project.

<a id="vscode" />

##### 3.1. VS Code

In you are using VS Code, change the `args` in the `Launch Main` configuration in `launch.json` file located at `.vscode` directory.

See the [main class](https://github.com/mohammadzainabbas/SDM-Lab-2/blob/main/src/main/java/Main.java) for the supported arguments.


<a id="terminal" />

##### 3.2. Terminal

Just run the following with the supported arguments:

```bash
sh scripts/build_n_run.sh exercise1
```

> Note: `exercise1` here is the argument which you'd need to run the first exercise

Again, you can check the [main class](https://github.com/mohammadzainabbas/SDM-Lab-2/blob/main/src/main/java/Main.java) for the supported arguments.