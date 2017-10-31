# SilentMe

## Overview

- SilentMe app will automatically silent user's mobile device on selected Locations. The user can select a location with the use of Google Places API(https://developers.google.com/places/) and  the app will store location's placeID to the database using content providers.

- The app will continuously check user's current location using Google geoLocation and PlaceDetectionClient.getCurrentPlace() method and whenever the user enters selected location app will notify the user to put their device to silent mode via Notifications. The user can silent phone from notification panel or simply reject the notification.

## Libraries

- Glid

- RxJava

- GooglePlayServicesPlaces

- GooglePlayServicesLocation

- Retrofit

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

- Google will not allow us to store places data more than 30 days except placeID. Whenecer the user selects location then this app stores placeId into SqLite Database using content providers.







