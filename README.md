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
---

<a id="introduction" />

#### 1. Introduction

__`Data drives the world.`__ In this big data era, the need to analyse large volumes of data has become ever more challenging and quite complex. Several different eco-systems have been developed which try to solve some particular problem. One of the main tool in Big Data eco system is the [_Apache Spark_](https://spark.apache.org/)


__Apache Spark__ analysis of big data became essential easier. Spark brings a lot implementation of useful algorithms for data mining, data analysis, machine learning, algorithms on graphs. Spark takes on the challenge of implementing sophisticated algorithms with tricky optimization and ability to run your code on distributed cluster. Spark effectively solve problems like fault tolerance and provide simple API to make the parallel computation.

<a id="graphx" />

##### 1.1 GraphX

[GraphX](https://spark.apache.org/docs/latest/graphx-programming-guide.html) is a new component in Spark for graphs and graph-parallel computation. At a high level, GraphX extends the Spark RDD by introducing a new Graph abstraction: a directed multigraph with properties attached to each vertex and edge.


This repository serves as a starting point for working with _Spark GraphX API_. As part of our SDM lab, we'd be focusing on getting a basic idea about how to work with `pregel` and get a hands-on experience with distributed processing of large graph.

<a id="pregel" />

##### 1.2 Pregel

Pregel, originally developed by Google, is essentially a message-passing interface which facilitates the processing of large-scale graphs. _Apache Spark's GraphX_ module provides the [Pregel API](https://spark.apache.org/docs/latest/graphx-programming-guide.html#pregel-api) which allow us to write distributed graph programs / algorithms. For more details, kindly check out the [original paper](https://github.com/mohammadzainabbas/SDM-Lab-2/blob/main/docs/pregel.pdf)

---

