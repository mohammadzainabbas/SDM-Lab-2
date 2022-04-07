
# General details

---

`Discussion about timetable and overall course structure`

	![](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/bcb89bab-0a3f-4771-9700-f8ccfdb017d9/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220407%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220407T133957Z&X-Amz-Expires=3600&X-Amz-Signature=0d1608b18d43326baa01efa14e9ed40894015ea97d19f8b4874895aa7ff90392&X-Amz-SignedHeaders=host&x-id=GetObject)

	> 💡 No mid-term exams

	> 💡 Exam - 40%  
	>Lab - 50%  
	>Project - 10%

---

# Introduction

---

> “Without data, you’re just another person with an opinion”  
>  
>- W. Edwards Deming (American statistician)

---

`New Business Model: 360˚ profile`

	- FB → more focused on content sharing (with friends, family etc)

	- Insta → more focused on photos sharing

	- WhatsApp → more focused on instant messages

	Based on these three, `meta (or facebook)` can create a 360˚ profile for every user to target you with their ads etc

---

`Decision Support Systems`

	⇒ IT systems that are transforming:

	> Data ⇒ Information ⇒ Knowledge

	---

	### DIKW Pyramid

	---

	![](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/a99f5158-f8e1-4987-b546-83567ed28c5d/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220407%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220407T134001Z&X-Amz-Expires=3600&X-Amz-Signature=7cae4316bae20c6b7f68f5cda3a01c7bc97aa13b497a8be21b397c309eafd6b8&X-Amz-SignedHeaders=host&x-id=GetObject)

	[bookmark](https://www.ontotext.com/knowledgehub/fundamentals/dikw-pyramid/)

	---

	[bookmark](https://www.ibm.com/downloads/cas/3RL3VXGA)

---

`Data Value Creation Chain`

	![Business Intelligence Lifecycle](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/ed1f1431-0038-4a66-bb96-92da6c2d71bb/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220407%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220407T134002Z&X-Amz-Expires=3600&X-Amz-Signature=d8d81bad99c89ca0cccdbb077b7646bf5bea992390220db933b29940f6f83125&X-Amz-SignedHeaders=host&x-id=GetObject)

	---

	### Data Lake

	---

	⇒ Storage repository that holds a vast amount of raw data in its native format until it is needed for analytics applications.

	---

	|Data Lake|Data Warehouse
	---|---|---
	Stored data|As it is in it’s native form: raw data|After transforming (ETL)
	Large data storage expensive ?|No, usually on cloud with distributed systems (Hadoop etc)|Yes, expensive to store large volume (need huge compute for ETL + need more space for the data warehouse and data marts etc)
	Schema|On read ⇒ Interpret schema when reading. Varies case to case|On write ⇒ Since mostly we use relational databases for data warehouses
	Storage + Compute|Two separate things (decoupled storage & compute). You storage as raw and when you need to compute, you just connect your compute resources|Highly coupled storage & compute. You can’t store without transforming (ETL) which requires high compute.  

	[bookmark](https://www.matillion.com/resources/blog/5-key-differences-between-a-data-lake-vs-data-warehouse)

	---

	### Polystore

	---

	⇒ A DBMS that is built on top of multiple, heterogeneous, integrated storage engines.

	⇒ Multiple storage engines are distinct and accessed separately through their own query engine.

	⇒ For example:

	- BigDAWG

		![](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/74e538e0-3179-46a9-bc7f-f124f490c40f/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220407%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220407T134004Z&X-Amz-Expires=3600&X-Amz-Signature=9dcab732d06f5a92a0c02715a8e13e43c7db2c33c24747428ced7698cbf5c884&X-Amz-SignedHeaders=host&x-id=GetObject)

		[bookmark](https://bigdawg-documentation.readthedocs.io/en/latest/intro.html)

---

> 🤦🏻 WHAT IS SEMANTICS ?  
>  
>Adding some meta-data (`what the data contains ?` `how to data was obtained ?` `what does it represents ?` etc) to the actual data to analyse it bit better.  
>  
>You can think of semantics as `annotations`

---

`Data Analysis Challenges`

	`Analyze any source`

		> 🤦🏻 Data can be in any form (structured like labeled data or unstructured like images/audio/text etc)

	`Right-time analysis`

		> 🤦🏻 We need to analyze it at the right time.  
		>  
		>And also, based on the business needs (Ad-hoc requirements)

---

`Use case studies`

	![](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/8fa9a2d1-d830-43bb-8950-79a1df14b3cc/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220407%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220407T134008Z&X-Amz-Expires=3600&X-Amz-Signature=1f6ec676b23a06955e681b65c0d509101a12060c18226d5e794d2e67d9ef494b&X-Amz-SignedHeaders=host&x-id=GetObject)

---

[bookmark](https://medium.com/mit-technology-review/this-is-why-a-i-has-yet-to-reshape-most-businesses-2f029d83b8d5)

---

`Challenges in Data Management`

	> 🤦🏻 WTH is Big Data ?  
	>  
	>`Volume`  
	>`Velocity`  
	>`Variety` (real challenge nowadays)  
	>`Veracity  
	>Value`

	Challenge is to intergrate different sources’ data and then analyze it taking into account lot of varieties of data

---

`Data Lake`

	> 💡 `LOAD FIRST, MODEL LATER`

	> ⚠️ **Add more details later**

---

`Challenges in Data Analytics`

	> Requires fixed input (for e.g: matrix etc) or some structure data or encoded data in order for algorithms to work

	> You need more domain knowledge first to properly analyze the data. Even in OpenData community, very few people know how to deal with that data

---
