# Separate test for /loot command
# Note: In Folia, loot spawn may trigger a NullPointerException
say "--- loot test start ---"
forceload add 0 0
loot spawn 0 100 0 loot minecraft:chests/simple_dungeon
say "--- loot test end ---"
