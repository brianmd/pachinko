# stopwatch

Fork of https://github.com/blandflakes/echo-stopwatch.


## Usage

> You: Alexa, open stopwatch

> Alexa: Stopwatch started

> You: Alexa, ask stopwatch for status

> Alexa: Your stopwatch has a duration of 30 seconds

> You: Alexa, tell stopwatch to stop

> Alexa: Your stopwatch had a duration of 45 seconds

> You: Alexa, reset my stopwach

> Alexa: No stopwatch is set, but I started a new one

> You: Alexa, reset my stopwatch

> Alexa: Stopwatch restarted. Previous duration was 15 seconds

## Observations

Switching on intent name is common - should provide a better mechanism for this.

## TODO
* Support pausing and resuming
* Offer to start a timer on status request if one isn't set
