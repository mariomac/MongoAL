MongoAL
=======

MongoDB Aggregation Language

It translates a query like this:

	FROM docs
		MATCH obj.prop > 3
		GROUP BY val1
			sum(val1) AS Field1
			sum(obj.prop) AS Field2
			sum(obj.prop + val1) AS FieldsSum
		SORT BY FieldsSum DESCENDING
        
Into something like this:

	db.docs.aggregate([
		{ "$match" : { "obj.prop" : { "$gt" : 3}}} , 
		{ "$group" : { "_id" : "$val1" ,
				"Field1" : { "$sum" : "$val1"} ,
				"Field2" : { "$sum" : "$obj.prop"} ,
				"FieldsSum" : { "$sum" : { "$add" : [ "$obj.prop" , "$val1"]}}}} ,
		{ "$sort" : { "FieldsSum" : -1}}
		]);

Didn't had time to document this. Please have a look to the [ANTLR4 Grammar file](src/main/MongoAL.g4) as a bad substitute.


## ---
Si te gustan mis aportaciones a github, quizás te gustará mi libro [Del bit a la Nube](http://www.xaas.guru/del-bit-a-la-nube/)
