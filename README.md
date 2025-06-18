# Overview

This is a small coding exercise based on a sample requirements document.  Pasting requirements overview:

> You will build a Java-based microservice (using any Java framework - for example
Spring Boot) that tracks “live” sports sportEvents and, for each live sportEvent, periodically (every
10 seconds) calls an external REST endpoint, transforms the response into a
message, and publishes it to a message broker (for example Kafka). You are free to
use any libraries, tools or frameworks - and you’re encouraged to leverage AI
assistants (e.g., ChatGPT, GitHub Copilot) - but you must review, validate, and
document any AI-generated output.
>
> Main Requirements
>
> * Expose a REST endpoint to receive sportEvent status updates (live ↔ not live).
> * For each sportEvent marked live, schedule a task to call an external REST API every
  10 seconds.
> * For the API you can assume that the API returns a json object with the
  following structure:
> ````
>    {
>        "eventId": "1234",
>        "currentScore": "0:0"
>    }
>````
>
> * Transform the API response into a message and publish it to a topic (for example
using Kafka).
> * Implement basic error handling and logging.
> * Deliver a working prototype along with documentation of your design choices, any
AI usage, guide for running.

There are additional detailed requirements not pasted here.

# Setup and Run

An jar file is provided or can be built from the sources.  For basic operation you will need to pass the details of:
* base URL of your live score service endpoint for requesting one event score
* your kafka cluster for writing output

Example (replace with your actual kafka and API locations):
    
    java -jar demo.jar -Dsportsapi.url=http://localhost:9100/eventScore/ -Dkafka.bootstrap=localhost:9092 

The application will listen on port 8080.  You can inform it of events and their live status by posting JSON, for example:

    curl -X POST -H "Content-Type:application/json" -d '{\"eventId\": \"9876\", \"status\": true}' http://localhost:8080/events/status

# Running Tests

A minimal set of JUnit5 tests are included, these can be executed using your IDE standard tools or:

    mvn test

# Summary of Design Decisions

* Chose Java and Spring Boot due to familiarity and suitability for the application
* Having chosen the framework, given the small scope most decisions are straightforward or dictated by the framework, for example using KafkaProducer and RestTemplate 
* Data model for external representation of sport event data is separate from internal model
* Used observer pattern via Spring Events to maintain separation between API polls, data store, and kafka push
* Kafka retries implemented by configuring built-in capabilities of KafkaProducer

# AI Statement

* Did not use any AI-generated code
* Used Spring Initializr and Intellij for some scaffolding purposes, but not for code logic