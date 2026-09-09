# Liahtina Test Datapack
# Tests /scoreboard /data /schedule /item /function commands
# /loot tested separately
# Can be tested without players (uses storage and #server scoreboard)

say "=== [Liahtina Test Datapack] Starting test ==="

# 0. forceload ensures chunk is loaded (needed for item test)
forceload add 0 0

# 1. /scoreboard test
say "--- Test 1: /scoreboard ---"
scoreboard objectives add test_kills dummy "Test Kills"
scoreboard objectives add test_deaths deathCount "Test Deaths"
scoreboard objectives add test_dummy dummy "Test Dummy"
scoreboard players set #server test_kills 0
scoreboard players add #server test_kills 5
scoreboard players set #server test_dummy 42
tellraw @a {"text":"[scoreboard] Created 3 objectives, #server test_kills=5","color":"gold"}

# 2. /data test - storage
say "--- Test 2: /data storage ---"
data modify storage test:main test_value set value "hello_from_datapack"
data modify storage test:main test_number set value 42
tellraw @a {"text":"[data] Written to storage test:main","color":"light_purple"}

# 3. /schedule test
say "--- Test 3: /schedule ---"
schedule function test:delayed 5s replace
tellraw @a {"text":"[schedule] Scheduled test:delayed in 5s","color":"red"}

# 4. /item test - forceload loaded chunk
say "--- Test 4: /item ---"
setblock 0 100 0 minecraft:chest
item replace block 0 100 0 container.0 with minecraft:diamond_sword
item replace block 0 100 0 container.1 with minecraft:netherite_helmet
tellraw @a {"text":"[item] Placed items into container at (0,100,0)","color":"yellow"}

say "=== [Liahtina Test Datapack] All command tests finished ==="
