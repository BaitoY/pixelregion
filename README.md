# Pixelregion - A SpongeForge & Pixelmon Plugin
_Sick of wild Pixelmon appearing on the overworld? Want to recreate the original feeling of wild encounters like the originals? Need a flexible, light-weight plugin to do the job for you?_
## What is this plugin?
This plugin aims to bring flexible, configurable wild events and regions to any Pixelmon server by recreating the original mechanics and allowing full customisation, all through a few JSON lines!

Currently, you can create:
  - Region creation:
    - Polygonal regions per world, with:
      - Custom display names.
      - Notification toggling.
      - Y-axis dimension support.
      - Weighting for overlapping regions.
      - Description and encounter info.
      - Multiple wild encounters, and Sweet Scent and Headbutt encounters.
      - Modifiable forage data.
      - Per-world support.
      - Event support.
  - Wild, Sweet Scent and Headbutt encounters:
    - Multiple conditions for encounters: Weather, Time, and Block(s) ontop and inside or targetted.
    - In-depth Pokemon customisation: Species, Weighting, Levels, Shiny chance and Boss chances.
  - Forage data:
    - Multiple conditions for foraging: Weather, Time, Pokemon Type and Block(s) targetted.
    - In-depth Reward customisation: Weighting, NBT support and Quantity.
  - Event Flags:
    - Through JSON, create Events that can be triggered within certain regions.
      - Simple but effective Condition > Trigger > Effect system.
      - Players hold a 'true' or 'false' value for every event, allowing for more condition checking.
      - Conditions consisting of: 
        - Event Flag Conditions - Check if the Player has _n_ flags enabled or disabled.
        - Party Conditions - Check the data of the Player's party or first Pokemon, with conditions like Species, Average, Ability and more!
        - World Conditions - Check the world of the Player, with conditions like Time, Weather and Block(s) ontop and inside. 
      - Triggers being Block Interact, Item Use and Automatic.
      - Effects consisting of:
        - Enabling, disabling and toggling flags.
        - Begin Pokemon battles, or spawn Pokemon in.
        - Give the Player an item.
        - Teach a certain Pokemon a move or apply a Spec.
        - Run commands. 
