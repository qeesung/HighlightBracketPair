package io.github.qeesung.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.util.Pair;

import java.util.*;

/**
 * Default Brace Highlighter to highlight all supported brace pair.
 */
public class DefaultBraceHighlighter extends BraceHighlighter {
    public static Map<Language, List<Pair<IElementType, IElementType>>>
            LanguageBracePairs = new HashMap<>();

    /**
     * Get all the registered languages' brace pairs and cache it.
     */
    static {
        Collection<Language> languageList = Language.getRegisteredLanguages();
        for (Language language :
                languageList) {
            PairedBraceMatcher pairedBraceMatcher =
                    LanguageBraceMatching.INSTANCE.forLanguage(language);
            if (pairedBraceMatcher != null) {
                BracePair[] bracePairs =
                        pairedBraceMatcher.getPairs();
                List<Pair<IElementType, IElementType>> braceList
                        = new LinkedList<>();
                if (bracePairs != null) {
                    for (BracePair bracePair :
                            bracePairs) {
                        Pair<IElementType, IElementType> braceEntry =
                                new Pair<>(
                                        bracePair.getLeftBraceType(),
                                        bracePair.getRightBraceType()
                                );
                        braceList.add(braceEntry);
                    }
                }
                LanguageBracePairs.put(language, braceList);
            }
        }
    }

    /**
     * Constructor.
     *
     * @param editor editor
     */
    public DefaultBraceHighlighter(Editor editor) {
        super(editor);
    }

    /**
     * Get all cached supported brace token pair.
     *
     * @return all supported brace pair token
     */
    @Override
    public List<Pair<IElementType, IElementType>> getSupportedBraceToken() {
        Language language = this.psiFile.getLanguage();
        List<Pair<IElementType, IElementType>> braceList = LanguageBracePairs.get(language);
        return braceList == null ? super.getSupportedBraceToken() : braceList;
    }
}
