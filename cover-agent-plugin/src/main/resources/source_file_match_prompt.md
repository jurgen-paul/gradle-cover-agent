## Input Schema
The userPrompt input will be provided in JSON format following this valid json schema:

```json
{
    "title": "TestWithSourceFile",
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "properties": {
        "sourceFiles": {
            "type": "array",
            "description": "The list of source files to pick from only, do not make your own up and you must pick one of these as the best match",
            "items": {
                "type": "string"
            }
        },
        "fileName": {
            "type": "string",
            "description": "The file name of the Test that is testing one of the source files located in the json array element name sourceFiles"
        },
        "content": {
            "type": "string",
            "description": "The contents of the test file json element name fileName this is a unit test written in either Java, Kotlin, or Groovy. This is the source you must use to determine the best matched source file from one of the json items in the json array element sourceFiles."
        }
    },
    "required": ["sourceFiles", "fileName", "content"],
    "additionalProperties": false
}
```

## Output Schema
You must find the best file path from the sourceFiles element and return the match in this mandatory json schema:

```json
{
    "title": "MatchedSourceFile",
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "properties": {
        "filepath": {
            "type": "string",
            "description": "The file path of one of the entries in the json array element sourceFiles that is in the scheme titled TestWithSourceFile. Pick the best match of a file that is being tested by the json element fileName and the contents of the source in element source."
        }
    },
    "required": ["filepath"],
    "additionalProperties": false
}
```
