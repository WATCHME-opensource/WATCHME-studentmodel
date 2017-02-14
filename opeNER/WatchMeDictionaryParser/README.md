# WatchMeDictionaryParser Application

This application must be used to convert CSV langauge dictionaries into JSON dictionary. This application automatically computes different Tag patterns so there is no need to put all possibilities in the dictionary. 

## Prerequisites 
 * You must install the latest opeNER open source platform, Read more at [install.md](../install.md) on how to install opeNER.
 * You must have a [CSV formatted](dictionary-sample-csv.md) language dictionary to use this package. if you don't have a CSV dictionary, you can read this documentation to start [preparing your CSV dictionary](#3). 
 * If you already have a [JSON formatted](sample-processed.md) Language Dictionary, Please visit [What's Next](#7) to find out what to do next.
 
 
 ## Related Readme files
 * [opeNER installation guide](../install.md)
 * [WatchMeRest API](../WatchMeRest/README.md)
 * [CSV formatted Dictionary](dictionary-sample-csv.md)
 * [JSON formatted Dictionary](sample-processed.md)
 * [Sample Config file](config.md)
 


##Preparing CSV Language Dictionary
First you will need to create a csv file containing the phrases and the phrase type for the language. in the following format:
```
<phrase_type>, <phrase> 
```
```
"Competency","Health advocate"
"Competency","Manager"
"Competency","Professional"
"Competency","Technical Skills"
"Negative","Below Level"
"Negative","low"
"Negative","totally disagree"
"Negative","disagree"
"Negative","poor"
"Negative","mistake"
"Negative","shortcoming"
"Improvement","At level"
"Improvement","good"
"Improvement","average"
"Improvement","at the same level"
```
Refer to [dictionary-sample.csv](dictionary-sample-csv.md) to see the Sample CSV dictionary. note: order of words in the phrase matters but the dictionary parser is case insensitive. Please try to avoid using replicated phrases, For example
**medical expert** and **Medical expert** are **the same so don't create two entries**, however expert medical and medical expert require seperate entries.

Once the Dictionary is ready, Follow the steps below:

* Create a Langauge folder for all languages you would like to work on. *note*:this folder should be used for all your languages.

* Create a specific folder for the lanaguage you are working on using the shortcode of the language code. en (English) , de (German) and nl (Dutch). 
if the folder for your lanaguage doesn't exist just create the language folder using it's two letter identifier (English: en, German: de , Dutch: nl). 
Note: remember you can only process English,Dutch and German using WatchMeRest API 


* Set the 'LanguageFolder' parameter in 'config.properties' to the folder path of the language folder you created in the previous path.

* Set the 'ProcessedLanguageFolder' parameter in 'config.properties' to the folder you wish the final json dictionary to be stored in.

* Depending on the complexity of your database specify the MaxGram Parameter. *note*: usually this value should be set to the maximum number of words the longest phrase contains.

* Set the Tokenizer, Part of Speech tagger and Language detector service paths. 
### Configuration file and NLP endpoints
 Please check the config.properties file [here](config.properties), to check what port numbers are set for the NLP service endpoints. <br>

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


* Once Completed you can copy all CSV dictionaries to the folder specified above([language_folder]/[lanaguage]). The filename used for the CSV file will be used as an identifier for the dictionary.


##Parse the CSV Dictionary
After Successful completion of the steps specified above, You can run the application in order to start parsing the dictionaries prepared in the previous section. You must pass 'lang' argument to the application using language shortcode (en/de/nl). 

**REMEMBER** OpenER services for tokenizer , tagger and lanaguage Identifier **MUST** be running for the parser to work.


## Parser Result
You can find the processed dictionaries for the language you have specified (using the 'lang' argument) under [processed_language_folder]/[lanaguage] folder. the resultant file should be similar to the following:
```
{
 "name": "MedicalTraining",
 "phrases": [
  {
   "tags": [
    "NN",
    "NN"
   ],
   "identifiers": [
    "health_NN",
    "advocate_NN"
   ],
   "phrase": "health advocate",
   "type": "Competency"
  },
  {
   "tags": [
    "NNP",
    "NN"
   ],
   "identifiers": [
    "Health_NNP",
    "advocate_NN"
   ],
   "phrase": "Health advocate",
   "type": "Competency"
  },
  {
   "tags": [
    "NN",
    "NNP"
   ],
   "identifiers": [
    "health_NN",
    "Advocate_NNP"
   ],
   "phrase": "health Advocate",
   "type": "Competency"
  },
  {
   "tags": [
    "NNP",
    "NNP"
   ],
   "identifiers": [
    "Health_NNP",
    "Advocate_NNP"
   ],
   "phrase": "Health Advocate",
   "type": "Competency"
  }],
 "lang": "en"
}
```

 You can see the full [here](sample-processed.md)

## What's next

Now, you are ready to setup the WatchMeRest API, [Click here to read the instruction on how to use WatchMeRest API](../WatchMeRest/README.md)
