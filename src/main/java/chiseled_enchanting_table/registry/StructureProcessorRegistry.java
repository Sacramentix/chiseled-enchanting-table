package chiseled_enchanting_table.registry;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.structureProcessor.BookshelfReplacerProcessor;


public class StructureProcessorRegistry {
    public static final Identifier BOOKSHELF_REPLACER_PROCESSOR_ID = ChiseledEnchantingTable.identifier("bookshelf_replacer");
    public static final StructureProcessorType<BookshelfReplacerProcessor> BOOKSHELF_REPLACER_PROCESSOR =
        StructureProcessorType.register(BOOKSHELF_REPLACER_PROCESSOR_ID.toString(), BookshelfReplacerProcessor.BOOKSHELF_REPLACER_PROCESSOR_CODEC);

    public static void init() {
        // Ensure the processor is registered
    }
}