Octo-Stalker
============
### My Solution
Application uses Android Annotations as a library to reduce redundancy of code as well convinience to:
- Switch between threads
- Use of Singletons
- Use of persistance properties
- xml view binding
- save instance state 

Information on this library can be found in : https://github.com/androidannotations/androidannotations

Note: given that the requirement for the following counter is expected to be run over a low number of users, first the activity gets a list of all the users and uses that information to display the UserListFragment. The fact that the client sees the information right away gives the sense of speed and responsiveness. In the meantime, the activity is finishing fetching the missing information (following counter) and updating the users within the list. 



Bypass Mobile Android exercise

###Application Specification

+ Display a list of user names and profile images that belong to the bypass organization.
  * Clicking on a member will display a list of user names and profile images of who they follow alphabetically.
  * This process repeats itself for the newly displayed user.
  * Searching should show a filtered list of users.
+ The cache library provided needs expiration and invalidation.

Feel free to improve on any code provided.

Note: If you start receiving 403 errors while developing it's becuase Github has a request limit of 60 queries per ip address per hour.

###[Mockups](./Mockups.pdf)

###Provided for you
- Models
- Image Loading
- Restclient

### Example

####HTTP Client Example

```java
getEndpoint().getOrganizationMember("bypasslane", new Callback<List<User>>() {
    @Override
    public void success(List<User> users, Response response) {}

    @Override
    public void failure(RetrofitError error) {}
});
```
####Load Image Example
```java
ImageLoader.createImageLoader(this)
 .load("http://.../name.jpeg")
 .into(image);
```
