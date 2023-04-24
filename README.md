# What is ItemPackage
ItemPackage is the PaperMC plugin that can generate crates containing random items at random locations. You can set items and spawn rates in the crate by using commands or by editing config files.

# Requirements
- Paper 1.18.2+

# Sample Usage

## Create items
- To create any item, Hold the item in the main hand and execute the following command 
    - `/ip exportitem <item-id>`
    - The item will be created as `item-id`
    - The item will be overwritten if `item-id` already exists
- To get or give the item, executing the following commands
    - `/ip getitem <item-id>`
    - `/ip giveitem <item-id> <player-id/selector> [amount]`
- To check created items or details of the specified item, executing the following command
    - `/ip items [item-id]`

## Create Lot and register items
- To create Lot and register items, execute the following command 
    - `/ip setlotitem <lot-id> <item-id> <rate>`
    - The lot will be created as `lot-id` if it doesn't exist
- To check created lots or details of the specified lot, execute the following command
    - `/ip lots [lot-id]`
    
## Spawn crates
- To spawn crates, execute the following command
    - `/ip wbplace3d <lot-id> <min-number-of-items> <max-number-of-items> <min-y> <radius> [amount]`
    - The crates will spawn at random locations within the specified radius centered at World Border
