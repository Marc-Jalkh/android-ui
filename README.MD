# KMM Project Startup 

A Collection of utils and tools to help you get started with a new KMM project.

## Installation

Add the following to your common `build.gradle.kts` file:

```kotlin
implementation("com.github.tomrrw.kmmProjectStartup:shared:0.0.1")
```

Then add the following to your android `build.gradle.kts` file:

```kotlin
implementation("com.github.tomrrw.kmmProjectStartup:shared-android:0.0.1")
```

`💡 Exposing shared library to IOS is important in case you need to use shared modules from this library to IOS`
[How to expose shared library to IOS](./README.MD#exposing-shared-library-to-ios)


## Usage

### Data Utils:

#### `Base API SERVICE`

A kotlin class that once inherited will provide a easy way to make Rest API calls.

```kotlin
class MyApi: BaseApiService(
    clientProvider = () -> HTTPClient(),
) {
    //use your REST API calls here
}
```

##### Methods:
```kotlin
get( urlString: String): Result<Model>
get( urlString: String, block: HttpRequestBuilder.() -> Unit): Result<Model>
post( urlString: String, block: HttpRequestBuilder.() -> Unit): Result<Model>
put( urlString: String, block: HttpRequestBuilder.() -> Unit): Result<Model>
delete( urlString: String): Result<Model>
```

#### `Entity Mapper`
interface that can be used to map data from one object to another.

```kotlin
class MyMapper: EntityMapper<Model, ModelDto> {
    override fun mapFromEntity(entity: Model): ModelDto = ...

    override fun mapToEntity(domainModel: ModelDto): Model = ...
}
```

#### `API to Date`
A function that can be used to convert a string to a date.

Api format: 2022-10-18 08:30:00
```kotlin
String.fromApiFormatToDate(): LocalDateTime
String.fromApiFormatToDate2(): LocalDateTime
```

#### `Loadable`
A sealed class that can be used to show if an object is loading even if the request is nullable`

```kotlin
type: Loadable<Object>
```
Methods: 
```kotlin
<T> Loadable.smartInit(data: T? = null)

Loaded(data: T)

NotLoaded<T>()
```
---
### Repository Utils:

#### `Get from caches helper`
Get data from a cache or a network call.

```kotlin
getFromCacheAndRevalidate(
    getFromCache: suspend () -> Result<Data>,
    getFromApi: suspend () -> Result<Data>,
    setInCache: suspend (Data) -> Unit,
    cacheAge: Instant?,
    revalidateIfOlderThan: Duration,
): Flow<Data>
```
---
---
### Domain Helpers:
### Models:
 A list of domain models that can be used in your project.

- AppConfig (version, name, platform, updateUrl?)
- AppPlatform | IOS, Android
- Duration | converts Durations to another unit
- Email(string) | Email address + regex validation
- FullName(salutation, first, last) | getFullName(), getFormattedName()
- OTP(4 * int) | provides from string to int and inverse
- Salutation | enum | Mr, Mrs, Ms, Dr, Prof and more...
- SocialLink(url:string, platform) | contains an enum of social platforms
- UpdateInfo(lastSupportedVersion, softUpdateDate, hardUpdateDate)
- Version(string) | version number + comparison
- PhoneNumber(string) | phone number + native platform validation(Android & IOS) with METHODS: 
fun getFormattedNumberInOriginalFormat(): String?,
fun isValid(): Boolean,
val region: Region
#### `ResultIOS`
transforms a Result into a ResultIOS<Data, Throwable>

ResultIOS is a class that has 2 states: Success and Failure, and can be used to handle the result of a network call.

```kotlin
fun <T> Result<T>.toResultIOS(): ResultIOS<T>
```

##### Methods:
```kotlin
fun ResultIOS.onSuccess(callback: (S) -> Unit)
fun ResultIOS.onFailure(callback: (F) -> Unit)
// takes 2 callbacks, one for success and one for failure
fun ResultIOS.fold(onSuccess: (S) -> Unit, onFailure: (F) -> Unit)
```

#### `Multiple field Validation Error`

```kotlin
class MultipleFieldValidationError(errors: Map<String, List < string>>)

//if the error is a multiple field validation error & it's length < 50,  it will return the error message else Something went wrong
fun Throwable.toMultipleFieldValidationError(): String
```
---
### UseCases:
#### `CompareStringsUseCase`
An object that can be used to compare 2 strings and return a score. This can be used in a search function to find similar strings in a list of strings.
It uses the combination of Levenshtein distance algorithm and Hamming distance to calculate the score.
Use case:
```kotlin
fun CompareStringsUseCase.findSimilarity(x: String,y: String): Double
```
---
### Utils:
#### `Thumbnail Url Helper`
A function that can be used to change the size of an image using its url and adding to its path a size parameter '200x200'...

```kotlin
ThumbnailUrlHelper().setImageShapeInUrl(shape:ImageShape, url: String): String

ImageShape {
    SQUARE -> 200x200
    RECTANGLE -> 1000x562
}
```

#### `UUID` custom string type named UUID

#### `Phone Util`
Some Phone utils native to Android and IOS only
```kotlin
val supportedRegions: List<Region>
fun getExamplePhoneNumberFor(region: Region): PhoneNumber?
```

#### `Validators`
A list of validators that can be used to validate data.

##### Rules:
- EmailRule
- PhoneNumberRule
- RequiredRule
- OTPRule
- TransformativeRule

And you can create your own rules by implementing the Rule class.

```kotlin
class Rule{
    fun isValid(value: String): Boolean
    fun isNotValid(value: String): Boolean
    fun getErrorMessage(language: ValidationStrings.Language): (fieldName: String) -> String
    fun getErrorMessageIfNotValid(value: Any?, language: ValidationStrings.Language ): ((fieldName: String) -> String)? 
}
```

Usage Of Validators:
```kotlin
// create a 
Field<Data>(
    val value: Data,
    val rules: List<Rule>,
    val name: String,
    val language: (default english)
)
//And you will be able to access the following methods
isValid: Boolean
fun getErrors(): List<String>?
fun getFirstError(): String?
//Also
fun List<Field<*>>.getErrors(): MultipleFieldValidationError?
// And if it obeys to transformative Rules you could use
//This is mostly used for transforming data to specific domain models
fun <Transformed> Field<*>.getTransformed(): Transformed
fun <Transformed> Field<*>.getTransformedOrNull(): Transformed?
```

---
---
### IOS Specific Methods:
#### `AppStoreHelper`
This class can be used to get the info of a specific app prividing the bundle Id of the app from the App Store.

```kotlin
class AppStoreHelper(httpClient: HttpClient, json: Json)
class AppStoreHelper(engine: engine, json: Json)
``` 
Methods:
```kotlin
fun getAppNameAndUpdateUrl(bundleId: String): AppInfo?
```
For the moment App Info contains the name and the updateUrl of the app.

## Exposing shared library to IOS
Just define a package implementation in your shared module and import unfound classes, models, implementations in your app shared module. So once the shared library is compiled, the IOS module will be able to access the shared library. 

`❗️Some modules or classes will be named Shared<Name of the module>`

example: 
```kotlin
var CompareStringsUseCase = CompareStringsUseCase //this is from the package!

fun Throwable.toUserFriendlyError() = this.toUserFriendlyError()

fun <S, F : Throwable> ResultIOS<S, F>.fold(onSuccess: (S) -> Unit, onFailure: (F) -> Unit) = this.fold(onSuccess, onFailure)

fun <S, F : Throwable> ResultIOS<S, F>.onFailure(callback: (F) -> Unit) = this.onFailure(callback)

fun <S, F : Throwable> ResultIOS<S, F>.onSuccess(callback: (S) -> Unit) = this.onSuccess(callback)
```