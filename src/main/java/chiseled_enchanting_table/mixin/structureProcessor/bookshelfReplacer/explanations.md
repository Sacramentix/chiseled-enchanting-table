# Concept

This folder aim to replace all bookshelf food in structure by chiseled bookshelf
that can contain enchanted book.

##  Multiple case

There is different structure that are handled differently.

### StructurePool.Projection case

For village house for example, we inject a custom StructureProcessor BookshelfReplacerProcessor
in the StructurePool.Projection class.
This handle all case where the structure is loaded raw from a file to the world.

### StructurePlacementData case

Some structure like mansion don't use StructurePool but rather StructurePlacementData.
Basically work the same we just inject BookshelfReplacerProcessor in the StructurePlacementData.
The custom processor will run for each room of the mansion.

### StructurePiece case

The last case is for structure like stronghold. Those structure are not loaded from a file, but rather
programmatically crafted in code by adding block one by one in the world.
For this case we mark all bookshelf block added for post processing.
Then in WorldChunk runPostProcessing we let a static method in BookshelfReplacerProcessor
handle the replacement of bookshelf.
