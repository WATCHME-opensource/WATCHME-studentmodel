# WatchMeRest API
This API offers simple Natural Language Processing Libraries such as Tokenization, Part of Speech Tagging and Lanaguage detection. Additionally, this library allows categorisation of phrases into predefined topics

## Prerequisites 
 * You must install the latest opeNER open source platform, Read more at [install.md](../install.md) on how to install opeNER. <br>
 * You must have a [CSV formatted](dictionary-sample-csv.md) or [JSON formatted](dictionary-sample.md) language dictionary. if you don't have a JSON dictionary but have the CSV dictionary, read [WatchMeDictionaryParser](../WatchMeDictionaryParser/README.md) for more details.

## Related Readme files
* [opeNER installation guide](../install.md)
* [WatchMeDictionaryParser](../WatchMeDictionaryParser/README.md)
* [CSV formatted dictionary](dictionary-sample-csv.md)
* [JSON formatted dictionary](dictionary-sample.md)
* [Sample Config file](config.md)

## Configuration file and NLP endpoints
 Please check the config.properties file [here](WebContent/WEB-INF/config.properties), to check what port numbers are set for the NLP service endpoints. <br>

```
UserAgent=watchme
LanguageIdentifierUrl = http://localhost:1236
TokenizerUrl = http://localhost:1234
TaggerUrl = http://localhost:1235
LanguageFolder = languages/
MaxGram = 4
ContextVar = lng_mgr

```
 'LanguageIdentifierUrl' is the parameter used for setting the Language detector endpoint (opeNER language detector), 
the port used here should be used to run the following command: <br><br>
```
language-identifier-server start -p <port-number-here>
```
'TokenizerUrl' is the parameter used for setting the tokenizer endpoint. the port number should be used for the following command. <br><br>
```
tokenizer-server start -p <port-number-here>
```
'TaggerUrl' is the parameter used for configuring the Part of Speach Tagger endpoint. Similarly the port number should be used for the command below: <br><br>
```
pos-tagger-server start -p <port-number-here>
```

## Language Dictionary File
It was discussed previously that WathcMeRest API also provides phrase categorisation capability, however, 
the usage of this service requires
predefined dictionary for the lanaguage and topic. 
There are two ways in order to create such dictionary:


1.you can use [dictionary-sample.json](dictionary-sample.md) template file in order to create your own JSON dictionary. <br>

2.or you can use the [WatchMeDictionaryParser Package](../WatchMeDictionaryParser/README.md) in order to convert your CSV dictionaries into JSON dictionaries (Highly Recommended). Read [WatchMeDictionaryParser Instructions](../WatchMeDictionaryParser/README.md) for more details. <br>





 Once the JSON dictionary file is ready you should copy the file to <font color=red>WebContent/WEB-INF/[folder]/[language]</font> 
 <b>Note:</b> [folder] gets the value of 'LangaugeFolder' configuration parameter, and [language] will be the shortcode for the language i.e. en (English), de (German) and nl (Dutch).

## Calling the API
You can send your requests to http://<tomcat-server>/WatchMeRest/api/LangaugeService/process - post request with parameter "text"
A reponse will look like the following code: <br><br>

```
{
  "input": "medical expert",
  "results": [
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "JJ",
          "NN"
        ],
        "identifiers": [
          "medical_JJ",
          "expert_NN"
        ],
        "phrase": "medical expert",
        "type": "Competency"
      }
    }
  ],
  "language": "en"
}

```
more complex inputs can be achieved but it all depends on the complexity of the Dictionary defined. 
the parameter **MaxGram** can be adjusted in order to adapt to more complex dictionaries and phrase matching.

Note: changing the **MaxGram** value to any value *larger* than **4**, may considerably effect the response time of the API. Try to avoid such an assignment as long as practically possible.

```
{
  "input": "technical skills above level but knowledge at the same level",
  "results": [
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "JJ"
        ],
        "identifiers": [
          "technical_JJ"
        ],
        "phrase": "technical",
        "type": "Other"
      }
    },
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "JJ",
          "NNS"
        ],
        "identifiers": [
          "technical_JJ",
          "skill_NNS"
        ],
        "phrase": "technical skills",
        "type": "Competency"
      }
    },
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "IN",
          "NN"
        ],
        "identifiers": [
          "above_IN",
          "level_NN"
        ],
        "phrase": "above level",
        "type": "Positive"
      }
    },
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "NN"
        ],
        "identifiers": [
          "knowledge_NN"
        ],
        "phrase": "knowledge",
        "type": "Other"
      }
    },
    {
      "dictionary_name": "MedicalTraining",
      "phrase": {
        "tags": [
          "IN",
          "DT",
          "JJ",
          "NN"
        ],
        "identifiers": [
          "at_IN",
          "the_DT",
          "same_JJ",
          "level_NN"
        ],
        "phrase": "at the same level",
        "type": "Improvement"
      }
    },
    {
      "dictionary_name": "TeacherEducation",
      "phrase": {
        "tags": [
          "NN"
        ],
        "identifiers": [
          "knowledge_NN"
        ],
        "phrase": "knowledge",
        "type": "Other"
      }
    }
  ],
  "language": "en"
}
```
