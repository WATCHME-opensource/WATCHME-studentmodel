# WATCHME Project: The Student Model Component

## General Description

This repository is a product of the WATCHME project. For documentation and background we refer to: [WATCHME](http://www.project-watchme.eu/).

## Description of the project

### Licence

The WATCHME project is released under MIT licence. Please refer to the LICENCE for more details.

### Requirements

#### Software

* Any modern Linux distribution, tested on Ubuntu 15.05 LTS. Most of the components are Windows compatible, but the targeted OS is Linux.
* **Oracle** Java, 1.8+
* Apache Tomcat, 8.x
* MongoDb, 2.6.x
* **(Optional)** Robomongo, 0.8.5+
* Maven

### Project architecture

Important files and folders of the service architecture:
```
opeNER\                                 The advanced Natural Language Processing module.
webapi\                                 This folder contains all Student Model modules and their implementation.
    api\	                            This module represents the entry point in the Student Model. It contains a set of Jersey RESTful web services and their dependencies. Additionally, it contains static files used by other modules.
    modules_ApiHelpGenerator\	        This module contains helper classes that can be used to generate content for the RESTful services help pages.
    modules_BayesianSM\	                This module is an adapter built around UnBBayes. It contains entities that translate Student Model queries to UnBBayes queries and also take care of mapping the response to something the Student Model can use.
    modules_Common\	                    Contains constants and domain classes used in all the other modules.
    modules_Components\	                This module is made up of smarter entities, most of which are part of the Student Model data processing pipeline (e.g. API Dispatcher, Data Merger). The clients for the EPASS APIs (forms, authentication and privacy manager) reside here as well.
    modules_DataAccess\	                Contains POJOs and data access components (data services and adapters) used by other modules and entities to interact with MongoDB.
    modules_DomainModel\	            This module contains all domain classes used by the other modules. If something (a POJO) is cross-cutting, then it should be here. Smarter components go into the Components module.
    modules_NaturalLanguageProcessor\	Adapter for OpeNER.
    modules_NumericalDataProcessor\	    Contains the NLP component, as defined in the Student Model architecture.
    modules_Tests\	                    Contains general purpose tests for all Student Model modules.
.gitignore	                            Global gitignore, for the entire project.
LICENSE	                                The license file.
README.md	                            General information 
```
#### Java libraries

The following libraries are needed to compile/run the source (also see the pom.xml files).
 
     activation-1.1
     cglib-nodep-2.2.2
     commons-logging-1.1
     commons-math3-3.6.1
     gson-2.3.1
     jackson-core-asl-1.9.13
     jackson-jaxrs-1.9.2
     jackson-mapper-asl-1.9.13
     jackson-xc-1.9.2
     javax.servlet-api-3.1.0
     jaxb-api-2.2.2
     jaxb-impl-2.2.3-1
     jersey-client-1.19
     jersey-core-1.19
     jersey-multipart-1.19
     jersey-server-1.19
     jersey-servlet-1.19
     jersey-json-1.19
     jettison-1.1
     jpf-1.5
     jsr311-api-1.1.1
     jstl-1.2
     junit-4.8.2
     logback-classic-1.1.3
     logback-core:1.1.3
     morphia-1.0.0
     mongo-java-driver-3.0.1
     mimepull-1.9.3
     org.osgi.core-4.1.0
     org.protege.common-4.1
     org.protege.editor.core.application-4.1
     org.protege.editor.owl-4.1.0.b209_2010_09_03_0303
     org.protege.owlapi.extensions-1.0.0
     org.protege.xmlcatalog-1.0.0
     org.semanticweb.HermiT-1.2.5.927
     org.semanticweb.owl.owlapi-3.1.0.1602
     powerloom-1.0
     protege-3.2
     protege-owl-3.2
     proxytoys-1.0
     slf4j-api-1.7.12
     stax-api-1.0-2
     stella-1.0
     unbbayes-4.18.10
     unbbayes.gui.mebn.ontology.protege-1.1.3
     unbbayes.prs.mebn-1.13.11
      
