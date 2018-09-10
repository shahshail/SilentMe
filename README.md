# SilentMe

## Overview
- This is an Android based application, aimed at offering comfort to those who wish to keep their mobile phones on silent mode in specific geographical locations. It has an appealing user interface with user-friendly features allowing the users to set their desired locations in their mobiles to turn silent.

- It’s simple to use application, useful in today’s scenario when people are extremely busy and want most of things automated to help in their functioning. This app will work as an aid to their day to day functioning! We often need to switch our mobile in silent mode for personal, social or professional reasons. We may forget to keep our mobile in silent mode that is where this app will come to the aid.

- You simply need to provide the geographical locations where you would like to keep your mobile into silent mode. Rest will be taken care of by this app. It will not forget to go in silent mode the moment it will enter the set radius of geo location.

- SilentMe app will automatically silent user's mobile device on selected Locations. The user can select a location with the use of Google Places API(https://developers.google.com/places/) and  the app will store location's placeID to the database using content providers.

## Libraries

- GooglePlayServicesPlaces

- GooglePlayServicesLocation


## Google Play Services API_KEY and Google API CLient

- Some Google Play Services APIs require you to create a client that will connect to Google Play Services and use that connection to communicate with the APIs. We can connect our app to Google Play Api client using GooglwPlayClient.Builder() Method.
![app txt](https://github.com/shahshail/SilentMe/blob/master/app/googlePlayApiClient.png)

- In order to use user's current location developer must login to google play developer console and generate Google Places API API_KEY(https://console.developers.google.com/apis/) and define this key into AndroidManifest.xml's meta-data field.
![app txt](https://github.com/shahshail/SilentMe/blob/master/app/api_key.png)

## Location Services aand GeoFences

- Using GPS Polling can drain device's battery. To overcome this problem this app will use Geofences to locate device's location.

- This app stores all the added locations latitudes and longitudes and setup 50 meter radius geofences using geofencing object. This geofence objects will automatically deleted in every 24 hours.

- This app requires location permission to locate current position and search places.

## Data Persistence

- Google will not allow us to store places data more than 30 days except placeID. So the most effiecient way to get location is to store placeId into database and get Location from placeId. 
- Whenever the user selects location then this app stores placeId into SqLite Database using content providers.

## Screens
### Home Screen
![app txt](https://github.com/shahshail/SilentMe/blob/master/home.png)

### Search Location
![app txt](https://github.com/shahshail/SilentMe/blob/master/search.png)

### Add Location
![app txt](https://github.com/shahshail/SilentMe/blob/master/result.png)

### Current Location
![app txt](https://github.com/shahshail/SilentMe/blob/master/map.png)

### Notification (Silent/Ring)
![app txt](https://github.com/shahshail/SilentMe/blob/master/noti.png)





