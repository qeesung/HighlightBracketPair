package io.github.qeesung.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.tree.IElementType;

import java.util.*;

public class DefaultBraceHighlighter extends BraceHighlighter {
    public static Map<Language, List<Map.Entry<IElementType, IElementType>>>
            LanguageBracePairs = new HashMap<>();

    static {
        Collection<Language> languageList = Language.getRegisteredLanguages();
        for (Language language :
                languageList) {
            PairedBraceMatcher pairedBraceMatcher =
                    LanguageBraceMatching.INSTANCE.forLanguage(language);
            if (pairedBraceMatcher != null) {
                BracePair[] bracePairs =
                        pairedBraceMatcher.getPairs();
                List<Map.Entry<IElementType, IElementType>> braceList
                        = new LinkedList<>();
                if (bracePairs != null) {
                    for (BracePair bracePair :
                            bracePairs) {
                        Map.Entry<IElementType, IElementType> braceEntry =
                                new AbstractMap.SimpleEntry<>(
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

    public DefaultBraceHighlighter(Editor editor) {
        super(editor);
    }

    @Override
    public List<Map.Entry<IElementType, IElementType>> getSupportedBraceToken() {
        Language language = this.psiFile.getLanguage();
        List<Map.Entry<IElementType, IElementType>> braceList = LanguageBracePairs.get(language);
        return braceList == null ? super.getSupportedBraceToken() : braceList;
    }
}
