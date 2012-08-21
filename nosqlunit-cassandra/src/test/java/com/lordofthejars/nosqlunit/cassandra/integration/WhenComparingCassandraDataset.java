package com.lordofthejars.nosqlunit.cassandra.integration;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static com.lordofthejars.nosqlunit.cassandra.EmbeddedCassandra.EmbeddedCassandraRuleBuilder.newEmbeddedCassandraRule;

import java.io.ByteArrayInputStream;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.junit.Rule;
import org.junit.Test;

import com.lordofthejars.nosqlunit.cassandra.CassandraAssertion;
import com.lordofthejars.nosqlunit.cassandra.CassandraConfiguration;
import com.lordofthejars.nosqlunit.cassandra.CassandraOperation;
import com.lordofthejars.nosqlunit.cassandra.EmbeddedCassandra;
import com.lordofthejars.nosqlunit.cassandra.InputStreamJsonDataSet;
import com.lordofthejars.nosqlunit.core.NoSqlAssertionError;

public class WhenComparingCassandraDataset {

	
	private static final String INSERT_DATA = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"defaultColumnValueType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"first\",\n" + 
			"                \"value\" : \"aaa\"\n" + 
			"            },{" +
			"				 \"name\": \"11\",\n" +
		    "				 \"value\" : \"bb\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_DIFFERENT_COLUMN_NAME = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"defaultColumnValueType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"aa\",\n" + 
			"                \"value\" : \"aaa\"\n" + 
			"            },{" +
			"				 \"name\": \"11\",\n" +
		    "				 \"value\" : \"bb\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_VALUES = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"defaultColumnValueType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"first\",\n" + 
			"                \"value\" : \"aaa\"\n" + 
			"            },{" +
			"				 \"name\": \"11\",\n" +
		    "				 \"value\" : \"cc\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_NUMBER_ROWS = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            },\n" + 
			"            {\n" + 
			"                \"name\" : \"12\",\n" + 
			"                \"value\" : \"12\"\n" + 
			"            }]\n" + 
			"        },\n" + 
			"        {\n" + 
			"            \"key\" : \"20\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"21\",\n" + 
			"                \"value\" : \"21\"\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_KEYSPACE = "{\n" + 
			"    \"name\" : \"MyKeyspace2\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"defaultColumnValueType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"first\",\n" + 
			"                \"value\" : \"aaa\"\n" + 
			"            },{" +
			"				 \"name\": \"11\",\n" +
		    "				 \"value\" : \"bb\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_MULTIPLE_COLUMNS_FAMILY = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"beautifulColumnFamilyName\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            },\n" + 
			"            {\n" + 
			"                \"name\" : \"12\",\n" + 
			"                \"value\" : \"12\"\n" + 
			"            }]\n" + 
			"        },\n" + 
			"        {\n" + 
			"            \"key\" : \"20\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"21\",\n" + 
			"                \"value\" : \"21\"\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    },{\n" + 
			"        \"name\" : \"beautifulColumnFamilyName2\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            },\n" + 
			"            {\n" + 
			"                \"name\" : \"12\",\n" + 
			"                \"value\" : \"12\"\n" + 
			"            }]\n" + 
			"        },\n" + 
			"        {\n" + 
			"            \"key\" : \"20\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"21\",\n" + 
			"                \"value\" : \"21\"\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_COLUMN_FAMILY ="{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"beautifulColumnFamilyName\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            },\n" + 
			"            {\n" + 
			"                \"name\" : \"12\",\n" + 
			"                \"value\" : \"12\"\n" + 
			"            }]\n" + 
			"        },\n" + 
			"        {\n" + 
			"            \"key\" : \"20\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"21\",\n" + 
			"                \"value\" : \"21\"\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_COLUMN_NUMBERS = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_MIXED_SUPER_COLUMN = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"	    \"columns\" : [{\n" + 
			"                \"name\" : \"11\",\n" + 
			"                \"value\" : \"11\"\n" + 
			"            }],\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_SUPER_COLUMN = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_SUPER_COLUMN_DIFFERENT_NAME = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1111\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_SUPER_COLUMN_WITH_DIFFERENT_COLUMN_NAME = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1111\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_SUPER_COLUMN_WITH_DIFFERENT_VALUE = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1111\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_WITH_SUPER_COLUMN_WITH_ONE_COLUMN = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_MULTIPLE_SUPER_COLUMNS = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [\n" + 
			"    {\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"        \"type\" : \"SUPER\",\n" + 
			"	\"keyType\" : \"UTF8Type\",\n" + 
			"	\"defaultColumnValueType\" : \"UTF8Type\",\n" + 
			"	\"comparatorType\" : \"UTF8Type\",\n" + 
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"10\",\n" + 
			"            \"superColumns\" : [{\n" + 
			"                \"name\" : \"1100\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1110\",\n" + 
			"                    \"value\" : \"1110\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1120\",\n" + 
			"                    \"value\" : \"1120\"\n" + 
			"                }]\n" + 
			"            },\n" + 
			"            {\n" + 
			"                \"name\" : \"1200\",\n" + 
			"                \"columns\" : [{\n" + 
			"                    \"name\" : \"1210\",\n" + 
			"                    \"value\" : \"1210\"\n" + 
			"                },\n" + 
			"                {\n" + 
			"                    \"name\" : \"1220\",\n" + 
			"                    \"value\" : \"1220\"\n" + 
			"                }]\n" + 
			"            }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	
	private static final String INSERT_DATA_DIFFERENT_TYPES = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"first\",\n" + 
			"                \"value\" : \"utf8(aaa)\"\n" + 
			"            },{" +
			"				 \"name\": \"age\",\n" +
		    "				 \"value\" : \"integer(20)\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	private static final String INSERT_DATA_DIFFERENT_TYPES_DIFFERENT_TYPE = "{\n" + 
			"    \"name\" : \"MyKeyspace\",\n" + 
			"    \"columnFamilies\" : [{\n" + 
			"        \"name\" : \"ColumnFamilyName\",\n" + 
			"		 \"keyType\" : \"UTF8Type\",\n"+
			"        \"comparatorType\" : \"UTF8Type\",\n"+
			"        \"rows\" : [{\n" + 
			"            \"key\" : \"jsmith\",\n" + 
			"            \"columns\" : [{\n" + 
			"                \"name\" : \"first\",\n" + 
			"                \"value\" : \"utf8(aaa)\"\n" + 
			"            },{" +
			"				 \"name\": \"age\",\n" +
		    "				 \"value\" : \"string(20)\"\n"+
			"			 }]\n" + 
			"        }]\n" + 
			"    }]\n" + 
			"}";
	
	@Rule
	public EmbeddedCassandra cassandraRule = newEmbeddedCassandraRule().build();
	
	@Test
	public void no_exception_should_be_thrown_if_dataset_is_expected() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA.getBytes())), cluster, keyspace);
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_column_name() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_DIFFERENT_COLUMN_NAME.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected name of column is aa but was not found."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_keyspace_name() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_KEYSPACE.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected keyspace name is MyKeyspace2 but was MyKeyspace."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_column_families_size() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_MULTIPLE_COLUMNS_FAMILY.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected number of column families is 2 but was 1."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_column_familiy_name() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_COLUMN_FAMILY.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(),is("Expected name of column family is beautifulColumnFamilyName but was not found."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_number_of_rows() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_NUMBER_ROWS.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected keys for column family ColumnFamilyName is 2 but was counted 1."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_number_of_columns() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_COLUMN_NUMBERS.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected number of columns for key jsmith is 1 but was counted 2."));
		}
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_column_value() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
		
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_VALUES.getBytes())), cluster, keyspace);
		} catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Row with key jsmith does not contain column with name 11 and value bb."));
		}
		
	}
	
	@Test
	public void no_exception_should_be_thrown_if_supercolumn_dataset_is_the_expected() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes())), cluster, keyspace);
		
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_number_of_super_columns() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_MULTIPLE_SUPER_COLUMNS.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(),is("Expected number of supercolumns for key 10 is 2 but was counted 1."));
		}
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_number_of_columns_into_super_column() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN_WITH_ONE_COLUMN.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Expected number of columns inside supercolumn 1100 for key 10 is 2 but was counted 1."));
		}
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_value_of_columns_into_super_column() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN_WITH_DIFFERENT_VALUE.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Row with key 10 and supercolumn 1100 does not contain expected column."));
		}
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_name_of_columns_into_super_column() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN_WITH_DIFFERENT_COLUMN_NAME.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Row with key 10 and supercolumn 1100 does not contain expected column."));
		}
	}
	
	@Test
	public void exception_should_be_thrown_if_dataset_contains_different_super_column_name() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN_DIFFERENT_NAME.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Supercolumn 1111 is not found into database."));
		}
	}
	
	@Test
	public void exception_should_be_thrown_if_standrad_columns_are_mixed_with_super_columns() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_WITH_SUPER_COLUMN.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		try {
			CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_WITH_MIXED_SUPER_COLUMN.getBytes())), cluster, keyspace);
		}catch(NoSqlAssertionError e) {
			assertThat(e.getMessage(), is("Standard columns for key 10 are not allowed because is defined as super column."));
		}
	}
	
	@Test
	public void no_exception_should_be_thrown_if_dataset_with_different_data_types_is_the_expected() {

		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_TYPES.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    
		CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_TYPES.getBytes())), cluster, keyspace);
		
	}
	
	@Test(expected=NoSqlAssertionError.class)
	public void exception_should_be_thrown_if_different_types_are_provided_between_expected_file_and_database() {
		
		CassandraOperation cassandraOperation = new CassandraOperation(new CassandraConfiguration("Test Cluster", "localhost", 9171));
		cassandraOperation.insert(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_TYPES.getBytes()));
		
		
	    Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9171");
		Keyspace keyspace = HFactory.createKeyspace("MyKeyspace", cluster);
	    

		CassandraAssertion.strictAssertEquals(new InputStreamJsonDataSet(new ByteArrayInputStream(INSERT_DATA_DIFFERENT_TYPES_DIFFERENT_TYPE.getBytes())), cluster, keyspace);
		
	}
	
}