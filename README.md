## Speichern
```
Gson gson = new Gson();
getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE).edit().putString("session", gson.toJson(mSession)).apply();
Log.println(Log.ASSERT, "XXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX PERSIST XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
```

## Laden
```
SharedPreferences sharedPreferences = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);

String session = sharedPreferences.getString("session", null);
if (session != null) {
  Gson gson = new Gson();
  Log.println(Log.ASSERT, "XXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX LOAD XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
  mSession = gson.fromJson(session, Session.class);
}
else {
  Log.println(Log.ASSERT, "XXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX LOAD NORMAL XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
  mSession = new Session(/* context= */ this);
}
```
