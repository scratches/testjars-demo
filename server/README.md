Run the main method in `ServerApplicationTests` to start the app and the auth server. Scrape the `AUTH_PORT` from the logs for the running auth server. Then grab a token:

```
$ AUTH_PORT=... # scrape from logs
$ TOKEN=$(curl -v spring:secret@localhost:$AUTH_PORT/oauth2/token -d grant_type=client_credentials -d client_id=spring -d client_secret=secret | jq -r .access_token)
```

Now you can use the token to access the server:

```

$ curl -H "Authorization: Bearer $TOKEN" localhost:8080/persons/test
test
```

