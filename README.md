# Memrise stats fetcher
Retrieves learned and total amount words from [Memrise](https://www.memrise.com/) and exposes this data as http endpoint using Kotless and AWS Lambda.

## API
- GET https://k8sg5btmo4.execute-api.eu-west-1.amazonaws.com/1 - returns stats for current day
- GET https://k8sg5btmo4.execute-api.eu-west-1.amazonaws.com/1/storage - returns historical data for previous days

## How to run
1. Please read [this article](https://hadihariri.com/2020/05/12/from-zero-to-lamda-with-kotless/) to get familiar with Kotless and create AWS account(not necessary for local run)
2. Store cookie from GET https://app.memrise.com/course/2022111/italian-1-32/ in `application.properties` file for local run. 
    Note that for deployment to AWS you will have to replace hardcoded value during setting headers, due to Kotless restrictions
3. Run `./gradlew local`, or Gradle kotless:local task from IDE
4. Navigate to http://localhost:8080/ in your browser. Response should look like this:
    ```
    {
        "2020-12-25": {
            "learnedWords": "239",
            "totalWords": "492",
            "longMemoryWords": "239",
            "createdAt": "2020-12-25T15:30:52.376",
            "error": null
        }
    }
    ```  
5. Run `./gradlew deploy` to deploy your project to AWS(after configuring AWS as described [here](https://hadihariri.com/2020/05/12/from-zero-to-lamda-with-kotless/))
  
   
## Links
- [Awesome article from Hadi Hariri](https://hadihariri.com/2020/05/12/from-zero-to-lamda-with-kotless/)
- [Kotless on Github](https://github.com/JetBrains/kotless)
