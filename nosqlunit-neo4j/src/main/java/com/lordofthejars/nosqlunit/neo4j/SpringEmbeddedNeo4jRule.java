package com.lordofthejars.nosqlunit.neo4j;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.context.ApplicationContext;

import com.lordofthejars.nosqlunit.core.PropertyGetter;

class SpringEmbeddedNeo4jRule extends Neo4jRule {

	private PropertyGetter<ApplicationContext> propertyGetter = new PropertyGetter<ApplicationContext>();
	
	public SpringEmbeddedNeo4jRule(Neo4jConfiguration neo4jConfiguration) {
		super(neo4jConfiguration);
	}

	public SpringEmbeddedNeo4jRule(Neo4jConfiguration neo4jConfiguration, Object object) {
		super(neo4jConfiguration, object);
	}

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object testObject) {
		this.databaseOperation = new Neo4jOperation(definedGraphDatabaseService(testObject));
		return super.apply(base, method, testObject);
	}
	
	private GraphDatabaseService definedGraphDatabaseService(Object testObject) {
		ApplicationContext applicationContext = propertyGetter.propertyByType(testObject, ApplicationContext.class);
		return applicationContext.getBean(GraphDatabaseService.class);
	}
	
}
