Started implementation for a link checker service, in order
to reduce the number of application job invalid links. 
For this, I employed the use of HttpClient, from the java.net
package. First tried an approach with one instance per thread 
which caused socket starvation. Then I replaced the strategy, 
and made one class shared instance, but that did not have the 
expected results either.
The issues seem to lay with the number of requests that the 
HttpClient has to send out and some issues regarding 
synchronisation for a large request pool.
A possible explanation for this behaviour is covered by this 
article: https://www.javaspecialists.eu/archive/Issue271-HttpClient-Executors.html

As of now further testing and research is required.

By prioritizing the issue at hand, another approach is necessary. 
As such, the service is downgraded to service_invalid package, 
and the replacing service package will contain a solution 
implemented using apache HttpClient.