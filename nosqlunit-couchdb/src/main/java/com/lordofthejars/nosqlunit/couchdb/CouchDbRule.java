package com.lordofthejars.nosqlunit.couchdb;

import static com.lordofthejars.nosqlunit.couchdb.ManagedCouchDbConfigurationBuilder.newManagedCouchDbConfiguration;

import org.ektorp.CouchDbConnector;

import com.lordofthejars.nosqlunit.core.AbstractNoSqlTestRule;
import com.lordofthejars.nosqlunit.core.DatabaseOperation;



public class CouchDbRule extends AbstractNoSqlTestRule {

	private static final String EXTENSION = "json";
	
	private DatabaseOperation<CouchDbConnector> databaseOperation;
	
	public static class CouchDbRuleBuilder {
		
		private CouchDbConfiguration couchDbConfiguration;
		private Object target;
		
		private CouchDbRuleBuilder() {
		}
		
		public static CouchDbRuleBuilder newCouchDbRule() {
			return new CouchDbRuleBuilder();
		}
		
		public CouchDbRuleBuilder configure(CouchDbConfiguration couchDbConfiguration) {
			this.couchDbConfiguration = couchDbConfiguration;
			return this;
		}
		
		public CouchDbRuleBuilder unitInstance(Object target) {
			this.target = target;
			return this;
		}
		
		
		public CouchDbRule defaultManagedMongoDb(String databaseName) {
			return new CouchDbRule(newManagedCouchDbConfiguration().databaseName(databaseName).build());
		}
		
		public CouchDbRule defaultManagedMongoDb(String databaseName, Object target) {
			return new CouchDbRule(newManagedCouchDbConfiguration().databaseName(databaseName).build(), target);
		}
		
		public CouchDbRule build() {
			
			if(this.couchDbConfiguration == null) {
				throw new IllegalArgumentException("Configuration object should be provided.");
			}
			
			return new CouchDbRule(couchDbConfiguration, target);
		}
		
	}
	
	public CouchDbRule(CouchDbConfiguration configuration) {
		super(configuration.getConnectionIdentifier());
		this.databaseOperation = new CouchDbOperation(configuration.getCouchDbConnector());
	}
	
	/*With JUnit 10 is impossible to get target from a Rule, it seems that future versions will support it. For now constructor is apporach is the only way.*/
	public CouchDbRule(CouchDbConfiguration configuration, Object target) {
		super(configuration.getConnectionIdentifier());
		setTarget(target);
		this.databaseOperation = new CouchDbOperation(configuration.getCouchDbConnector());
	}
	
	
	@Override
	public DatabaseOperation<CouchDbConnector> getDatabaseOperation() {
		return databaseOperation;
	}

	@Override
	public String getWorkingExtension() {
		return EXTENSION;
	}

}
