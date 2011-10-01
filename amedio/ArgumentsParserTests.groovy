import org.junit.Test

import static org.junit.Assert.*

class ArgumentsParserTests {
	
	@Test
	void shouldParseBooleanArgument() {
		
		def parser = new ArgumentsParser("Bl")
		parser.parse("-l")
		
		assertTrue parser.getValue("l")
	}
	
	@Test
	void shouldReturnDefaultBooleanValue() {
		
		def parser = new ArgumentsParser("Bl")
		parser.parse("")
		
		assertFalse parser.getValue("l")
	}
	
	@Test
	void shouldParseIntegerArgument() {
		
		def parser = new ArgumentsParser("In")
		parser.parse("-n 9")
		
		assertEquals 9, parser.getValue("n")
	}
	
	@Test
	void shouldReturnDefaultIntegerValue() {
		
		def parser = new ArgumentsParser("In")
		parser.parse("")
		
		assertEquals 0, parser.getValue("n")
	}
		
	@Test
	void shouldParseStringArgument() {

		def parser = new ArgumentsParser("Sc")
		parser.parse("-c hello")

		assertEquals "hello", parser.getValue("c")
	}
	
	@Test
	void shouldReturnDefaultStringValue() {
		
		def parser = new ArgumentsParser("Sc")
		parser.parse("")
		
		assertEquals "", parser.getValue("c")
	}
	
	@Test
	void shouldParseMixedValues() {

		def parser = new ArgumentsParser("Bl In Sc")
		parser.parse("-l -n 1 -c hello")
		
		assertTrue parser.getValue("l")
		assertEquals 1, parser.getValue("n")
		assertEquals "hello", parser.getValue("c")
	}
	
	@Test
	void shoulFailWhenFlagNotInArguments() {

		def parser = new ArgumentsParser("Bl In Sc")
		try {

			parser.parse("-f -n 1 -c hello")
			fail("Exception must be thrown")

		} catch (IllegalArgumentException e) {
			
			assertEquals "flag -f not allowed", e.message
		}
	}
}

class ArgumentsParser {

	private final Boolean DEFAULT_BOOLEAN_VALUE = false
	private final Integer DEFAULT_INTEGER_VALUE = 0
	private final String DEFAULT_STRING_VALUE = ""
	
	String schema
	Map values
	
	ArgumentsParser(String schema) {
		this.schema = schema
		this.values = [:]
	}
	
	def parse(String arguments) {
		
		List<String> argumentsList = splitArguments(arguments)
		splitSchema(schema).each { schemaEntry ->

			String flag = getFlagFromSchemaEntry(schemaEntry)
			
			if(isBoolean(schemaEntry)) {

				setBooleanFlagValue(flag, argumentsList)
				
			} else if (isInteger(schemaEntry)) {
				
				setIntegerFlagValue(flag, argumentsList)
				
			} else if (isString(schemaEntry)) {
				
				setStringFlagValue(flag, argumentsList)
			}
		}
	}
		
	private List<String> splitArguments(String arguments) {
		return arguments.split(" ")
	}
	
	private List<String> splitSchema(String schema) {
		return schema.split(" ")
	}
	
	private void setIntegerFlagValue(String flag, List argumentsList) {

		setFlagValue(flag, DEFAULT_INTEGER_VALUE)
		if(containsFlag(argumentsList, flag)) {
			setFlagValue(flag, Integer.valueOf(getValueFromFlag(argumentsList, flag)))
		}
	}
	
	private void setBooleanFlagValue(String flag, List argumentsList) {
		
		setFlagValue(flag, DEFAULT_BOOLEAN_VALUE)
		if (containsFlag(argumentsList, flag)) {
			setFlagValue(flag, true)
		}
	}
	
	private void setStringFlagValue(String flag, List argumentsList) {
	
		setFlagValue(flag, DEFAULT_STRING_VALUE)
		if (containsFlag(argumentsList, flag)) {
			setFlagValue(flag, getValueFromFlag(argumentsList, flag))
		}
	}
	
	private getValueFromFlag(List argumentsList, String flag) {
		return argumentsList.get(argumentsList.indexOf("-" + flag) + 1)
	}
	
	private isString(String schemaEntry) {
		return getTypeFromSchemaEntry(schemaEntry) == "S"
	}
	
	private isInteger(String schemaEntry) {
		return getTypeFromSchemaEntry(schemaEntry) == "I"
	}
	
	private isBoolean(String schemaEntry) {
		return getTypeFromSchemaEntry(schemaEntry) == "B"
	}
	
	private getFlagFromSchemaEntry(String schemaEntry) {
		return schemaEntry.substring(1)
	}
	
	private getTypeFromSchemaEntry(String schemaEntry) {
		return schemaEntry.substring(0, 1)
	}
	
	private Boolean containsFlag(List argumentsList, String flag) {
		return argumentsList.contains("-" + flag)
	}
	
	private void setFlagValue(String flag, def value) {
		this.values[flag] = value
	}
		
	def getValue(String flag) {
		return values[flag]
	}
}