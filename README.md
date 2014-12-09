MongoAL
=======

MongoDB Analytics Language

To do
-----

* Use $exists operator in queries: http://docs.mongodb.org/manual/reference/operator/query/exists/#op._S_exists
* Unit testing with Embedded Mongo: http://www.hascode.com/2013/10/writing-java-integration-tests-for-mongodb/ or similar


Sobre ejemplo primero en http://docs.mongodb.org/manual/core/aggregation-pipeline/

	FROM orders
	MATCH status = "A"
	GROUP cust_id as _id,
		  sum(amount) as total

Esto devolvería un array de results del estilo {_id:"A123",total:750}

Vamos a soportar las siguientes stages de aquí: http://docs.mongodb.org/manual/reference/operator/aggregation/#aggregation-pipeline-operator-reference

	GROUP <expr> as _id,
		  <accumulator>(<expression>) as fieldName

// group DEBE incluir un campo _id en mongodb, pero puede ser null para que lo calcule todo. Si aquí no se especifica, que sea null

MATCH <query>

	<query>:

	<campo> = <valor> equivale a { <campo> : <valor>}

	<campo> in ['val1','val2']
		equivale a
		{ <campo> : { $in : ['val1','val2']}}

	<cond> AND <cond> ejemplo
		type = 'food' AND price < 9.95
		equivale a
		{ type: 'food', price: { $lt: 9.95 } }

	<cond> OR <cond> ejemplo
		qty > 100 OR price < 9.95
		equivale a
		{ $or: [ { qty: { $gt: 100 } }, { price: { $lt: 9.95 } } ] }

DE MOMENTO No soportaremos queries sobre embedded documents

Se puede buscar por un campo que está en una rama de un árbol (embedded document):

	producer.company = 'abc123'
	equivale a
	{ 'producer.company': 'ABC123' }

DE MOMENTO no haremos busquedas sobre arrays, pero sí sobre sus elementos. Por ejemplo:

	ratings[0] = 5
	equivale a
	{ 'ratings.0': 5 }

PROJECT <pecifications>
	DE MOMENTO no lo haremos
	(Considerar hacer un project automático entre stages para solo procesar lo que necesitamos)

SORT BY <field1> ASCENDING [<field2> DESCENDING]

	SORT BY age ASCENDING, weight DESCENDING
	equivale a
	{ $sort : { age : 1, weight -1}}

UNWIND DE momento NO, pero puede ser interesante en el futuro. Quizás hay que hacerlo indirectamente cuando queramos hacer busquedas sobre elementos de arrays




	
	<accumulator>

	<expression>