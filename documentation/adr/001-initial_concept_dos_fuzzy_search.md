001 - DoS Service Fuzzy Search API
----------------------------------

* Date: 2021/06/29
* Status: Agreed
* Deciders: Daniel Stefaniuk, Jonathan Pearce

## Context

Currently searches of the DOS datastore requires the client to have some knowledge of the services that are being searched for as searches are completed with an exact match search. This results in intermittent false negative searches and a less flexible search.

## Decision

The DoS Service Fuzzy Search API applies a search on the DoS datastore with fuzzy matching on a select four attributes.

## Consequences

This results in more intuitive searches as the fuzzy logic allows for more performant searches, elimination of false negative results and the non-exact matches allows for relevant results to be returned as well as the specific results.
