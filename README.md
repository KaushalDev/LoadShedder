# LoadShedder

Loadshedder provides basic load surge protection in spring boot API.

### How to use it.

1. Add LoadShedder module as dependency.
1. Add following anotation to controller @RequestThrottler(limitPerSecond = 10)
