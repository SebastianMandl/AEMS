# aems-apilib
This library is used to generate the JSON-Body that will be sent to the AEMS-API. It also provides methods to communicate to the API and encryption support.

# General Usage
The usage of the API-LIB is fairly simple. You start by telling the LIB the address of the rest service. 
```java
AemsAPI.setUrl("http://your-rest-service.net/Rest");
/* Other timeout setting (default is 5 seconds) */
AemsAPI.setTimeout(10000);      // 10 seconds timeout
```
Then you pick which action you want to perform. There are predefined actions for many operations, like: 

* AemsQueryAction
* AemsInsertAction
* AemsUpdateAction
* AemsDeleteAction
* AemsLoginAction
* AemsRegisterAction
* AemsStatisticAction

You can read about the specific usage of each action below. Let's just assume you have picked the `AemsQueryAction`. Your code
can look something like this:
```java
AemsUser myUser = new AemsUser(userId, username, password);
AemsQueryAction query = new AemsQueryAction(myUser);
query.setQuery("{ graphql { id, query } }");
```
Great, you have prepared your action, which will generate the required JSON string. Now let's actually communicate with the API!
```java
AemsResponse response = AemsAPI.call0(query, null);
if(!response.isOk()) {
  System.err.println("AemsResponse reports failed api call: " + response.getException());   // troubleshoot the problem
}
String rawText = response.getResponseText();            // This text is most likely encrypted and base 64 encoded.
String plainText = response.getDecryptedResponse();

// If you are using GSON as your JSON framework, you have some convenience methods available:
JsonObject o = response.getAsJsonObject();
JsonArray a = response.getAsJsonArray();
```

# Examples (not completely up-to-date!)
## No-Auth Actions
Please follow [this link](https://github.com/GitGraf/aems-apilib/wiki/No-Authentication-Actions) to read about actions that do not require full authentication.
## Regular Actions
Please follow [this link](https://github.com/GitGraf/aems-apilib/wiki/Aems-Actions) in order to read about actions that require authentication.
