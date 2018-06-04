# Project #2

In order to compile the project, type:
```
javac -deprecation *.java
```

To run the program:
```
java ProxyCache [port number]
```
*Keep in mind that the browser must be configured to localhost and
the port number you choose if you are testing locally*

## Testing
We tested our program using *heather.cs.ucdavis.edu*, *heather.cs.ucdavis.edu/Matloff.html*,
and an web page with no response body. 
We did notice that our program would crash due to the *BufferedReader* in HttpRequest, which we traced back to being an
error with the data sent from the client socket. Additionally, the socket would sometimes error out when sending a response
from a web page back to a client. 

## Extentions
We implemented the following extentions to our basic proxy:
**Better Error Handling**, **Support for POST-method**, and **Caching**.

### Better Error Handling
After getting a response from the server we are sending a request to, we check to see if there is a response body by
checking to see if there are any *bytes != 0*. If there exists bytes other than 0, then there is a body. Additionally,
we cached this request so that any further requests to this page will not waste resources attempting to get an empty 
body.

### Support for POST-method
In **HttpRequest.java**, we test if we have a post request by checking if the *method* is "POST". If it is, then we read
the header content as usual, but keep track of the *content-length* in order to get the number of bytes inside the body,
which is the POST data. We then check to see if we do have to read the body for POST data. If we do, then we simply 
parse the data, so it is readily available for ProxyCache to retrieve it to send to the server.

### Caching
We used a simple HashMap to cache our objects. The key is *HttpRequest.toString() method*. The value is *HttpResponse*.
We did not include a TTL for our objects in our cache.
```
private static Map<String, HttpResponse> cache;
```
When we access a page for the first time, we cache all the requests and responses into our cache. The next time we 
request those page(s), we immediately search through the cache to see if it exists already. If it does, then we simply
return the associated *HttpResponse* object and return immediately. 



