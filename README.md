<img src="icon.png" width="100" height="105" align="right" alt="reloadly-java-icon"/>

<!--
  Title: Reloadly SDK for Java
  Description: Reloadly Java SDK for sending Airtime Topups to over 4 billion mobile phones.
  Author: Reloadly
  -->

# Reloadly SDK for Java

[![CircleCI][circle-ci-badge]][circle-ci-url]
[![MIT][mit-badge]][mit-url]
[![Maven Central][maven-badge]][maven-url]

The **Reloadly SDK for Java** enables Java developers to easily work with [Reloadly Services][reloadly-main-site]
and build scalable solutions. You can get started in minutes using Maven or any build system that supports MavenCentral
as an artifact source.

* [SDK Homepage][sdk-website]
* [Sample Code][sample-code]
* [API Docs][docs-api]
* [Issues][sdk-issues]
* [Giving Feedback](#giving-feedback)
* [Getting Help](#getting-help)

## Getting Started

#### Sign up for Reloadly ####

Before you begin, you need a Reloadly account. Please see the [Sign-Up for Reloadly][reloadly-signup-help] section of
the knowledge-base for information about how to create a Reloadly account and retrieve
your [Reloadly APIs credentials][api-credentials-help].

#### Minimum requirements ####

To run the SDK you will need **Java 1.8+**

## Using the SDK Modules

The SDK is made up of several modules such as **Authentication, Airtime, Giftcard etc...**, you can alternatively add
dependencies for the specific services you use only. For example : Authentication & Airtime
***(currently all modules have the same version, but this may not always be the case)***

### Gradle users

Add specific dependencies to your project's build file:

```groovy
implementation "software.reloadly:java-sdk-authentication:1.0.3"
```

**OR**

```groovy
implementation "software.reloadly:java-sdk-airtime:1.0.3"
```

**OR**

```groovy
implementation "software.reloadly:java-sdk-giftcard:1.0.3"
```

### Maven users

Add specific dependencies to your project's POM:

```xml

<dependency>
    <groupId>software.reloadly</groupId>
    <artifactId>java-sdk-authentication</artifactId>
    <version>1.0.3</version>
</dependency>
```

**OR**

```xml

<dependency>
    <groupId>software.reloadly</groupId>
    <artifactId>java-sdk-airtime</artifactId>
    <version>1.0.3</version>
</dependency>
```

**OR**

```xml

<dependency>
    <groupId>software.reloadly</groupId>
    <artifactId>java-sdk-giftcard</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Others

You'll need to manually install the following JAR :

- The Reloadly JAR from <https://github.com/reloadly/reloadly-sdk-java/releases/latest>

## Getting Help

GitHub [issues][sdk-issues] is the preferred channel to interact with our team. Also check these community resources for
getting help:

* Checkout & search our [knowledge-base][reloadly-knowledge-base]
* Talk to us live on our chat tool on our [website][reloadly-main-site] (bottom right)
* Ask a question on [StackOverflow][stack-overflow] and tag it with `reloadly-java-sdk`
* Articulate your feature request or upvote existing ones on our [Issues][features] page
* Take a look at our [YouTube series][youtube-series] for plenty of helpful walkthroughs and tips
* Open a case via with the [Reloadly Support Center][support-center]
* If it turns out that you may have found a bug, please open an [issue][sdk-issues]

## Documentation

Please see the [Java API docs][api-docs] for the most up-to-date documentation.

You can also refer to the [online Javadoc][javadoc].

The library uses [Project Lombok][lombok]. While it is not a requirement, you might want to install
a [plugin][lombok-plugins] for your favorite IDE to facilitate development.

## Running Tests

There are 3 groups of tests : `integration`, `integration-with-proxy` and all test.

* To run all the tests, including integration tests, execute `./mvnw test`. However, for integration tests to run
  successfully, the following environment variables are required :

```
export LIVE_CLIENT_ID=put_your_account_live_client_id_here
export LIVE_CLIENT_SECRET=put_your_account_live_client_secret_here
export SANDBOX_CLIENT_ID=put_your_account_sandbox_client_id_here
export SANDBOX_CLIENT_SECRET=put_your_account_sandbox_client_secret_here
export PROXY_HOST=put_your_proxy_host_here
export PROXY_USERNAME=put_your_proxy_username_here
export PROXY_PASSWORD=put_your_proxy_password_here
export PROXY_PORT=put_your_proxy_port_here
```

* To run all integration tests only, execute `./mvnw test -Dgroups=integration`

* To run all proxy integration tests only, execute `./mvnw test -Dgroups=integration-with-proxy`

## Giving Feedback

We need your help in making this SDK great. Please participate in the community and contribute to this effort by
submitting issues, participating in discussion forums and submitting pull requests through the following channels:

* Submit [issues][sdk-issues] - this is the preferred channel to interact with our team
* Come join the Reloadly Java community chat on [Slack][slack]
* Articulate your feature request or upvote existing ones on our [Issues][features] page
* Send feedback directly to the team at oss@reloadly.com

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.

[reloadly-main-site]: https://www.reloadly.com/

[sdk-website]: https://docs.reloadly.com/devtools/toolbox/libraries-and-sdks

[reloadly-signup-help]: https://faq.reloadly.com/en/articles/2307724-how-do-i-register-for-my-free-account

[api-credentials-help]: https://faq.reloadly.com/en/articles/3519543-locating-your-api-credentials

[sdk-issues]: https://github.com/reloadly/reloadly-sdk-java/issues

[sdk-license]: http://www.reloadly.com/software/apache2.0/

[slack]: https://reloadly-developers.slack.com/

[sample-code]: https://github.com/reloadly/reloadly-sdk-java/blob/master/SAMPLE-CODE.md

[docs-api]: https://developers.reloadly.com

[features]: https://github.com/reloadly/reloadly-sdk-java/issues?q=is%3Aopen+is%3Aissue+label%3A%22feature-request%22

[api-docs]: https://developers.reloadly.com

[javadoc]: https://reloadly.dev/reloadly-java

[lombok]: https://projectlombok.org

[lombok-plugins]: https://projectlombok.org/setup/overview

[mit-badge]: http://img.shields.io/:license-mit-blue.svg?style=flat

[mit-url]: https://github.com/reloadly/reloadly-sdk-java/raw/master/LICENSE

[maven-badge]: https://maven-badges.herokuapp.com/maven-central/software.reloadly/java-sdk/badge.svg

[maven-url]: https://maven-badges.herokuapp.com/maven-central/software.reloadly/java-sdk

[circle-ci-badge]: https://circleci.com/gh/Reloadly/reloadly-sdk-java.svg?style=svg&circle-token=f06dbc5f2511715447dd8d62ff00065cb245701e

[circle-ci-url]: https://circleci.com/gh/Reloadly/reloadly-sdk-java/tree/main

[codecov-badge]: https://codecov.io/gh/reloadly/reloadly-sdk-java/branch/main/graph/badge.svg?token=8U89VKQ2BF

[codecov-url]: https://app.codecov.io/gh/reloadly/reloadly-sdk-java

[youtube-series]: https://www.youtube.com/watch?v=TbXC4Ic8x30&t=141s&ab_channel=Reloadly

[reloadly-knowledge-base]: https://faq.reloadly.com

[stack-overflow]: http://stackoverflow.com/questions/tagged/reloadly-reloadly-sdk

[support-center]: https://faq.reloadly.com/en/articles/3423196-contacting-support
