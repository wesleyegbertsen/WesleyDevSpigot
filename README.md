# WesleyDevSpigot
A Minecraft Spigot plugin for my WesleyDev Minecraft servers.

## Plugin features:
- Rewarding players for killing monsters
    - Reward based on the dropped XP from a monster.
    It's randomly generated based on the following minimum and maximum.
        - Minimum reward amount is 20% of the dropped XP
        - Maximum reward amount is 80% of the dropped XP multiplied by the following modifier:
            - The modifier is based on the monsters health, 
            and is randomly generated with a minimum and maximum.
            The minimum of this modifier is
            10% of the max health of the monster divided by 2. This ensures that minimum is
            1 for vanilla monsters (max health of vanilla monster is 20).
            But monsters from plugins with higher health than normal have a 
            higher minimum. The max of this modifier is the max health
            of the monster divided by 2.
        - The reward can be summed up by the following formula, where 'x' = dropped experience, 
        'h' = maximum monster health, and rand() = a random number for the given range 
        (',' is the delimiter for the range).
            - Formula: rand(0.20 * x, (0.80 * x) * (rand((0.10 * h) / 2), h / 2))
    - Rewarded with an economy through the Vault API: https://github.com/MilkBowl/VaultAPI
- Players can buy diamonds for $1000 each with the command: "/wesleydev buy diamond [amount]"
- More features are work in progress

## Why this plugin?
I've started developing this plugin, 
because I love hooking into a game api and use the api to enhance the gameplay.
When I was a teenager I used to develop a few plugins for Bukkit. 
So you can also say this is kinda a nostalgia trip. 
I've set up a survival and creative Minecraft server on my personal developer VPS, 
to test and play the features this plugin provides.

## Minecraft Servers
The status of my Minecraft servers are available here: https://stats.mc.wesleydev.nl. 
Here you can see some metrics of my servers, and also a live view of the world maps.