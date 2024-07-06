# infrium
This project is a minecraft Network setup using Velocity as the proxy, Redis as the in-memory pub/sub channel providor, mongodb as the database provider

Requires Java 21 to build

cloud requirements:

- JRE 21 (AdoptOpenJDK is used)
- screen (screen -mS hive/velocity)
- ufw (firewall)
- mongodb
- redis

Auto launch Lobbies

Selectively host permanent servers



Planned features:

Patch rollout (rollout patches without taking down the entire network, juggle players)