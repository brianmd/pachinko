# pachinko

Fork of https://github.com/blandflakes/echo-stopwatch.

Miscellaneous functions to ask Amazon's Alexa. For example, the first function is for my wife, who enjoys watching the space station fly overhead, so it will tell her when the next sighting will happen.

## Usage

> You: Alexa, ask pachinko for next space station sighting

> Alexa: The next space station sighting in Albuquerque, New Mexico will be tomorrow at 8:42 PM.

> You: Alexa, open pachinko

> Alexa: Pachinko started

> You: Alexa, ask pachinko for status

> Alexa: Your pachinko has a duration of 30 seconds

> You: Alexa, tell pachinko to stop

> Alexa: Your pachinko had a duration of 45 seconds

> You: Alexa, reset my stopwach

> Alexa: No pachinko is set, but I started a new one

> You: Alexa, reset my pachinko

> Alexa: Pachinko restarted. Previous duration was 15 seconds

## Observations

Switching on intent name is common - should provide a better mechanism for this.

## TODO
* Support pausing and resuming
* Offer to start a timer on status request if one isn't set
