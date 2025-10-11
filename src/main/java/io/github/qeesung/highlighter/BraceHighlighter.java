package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.adapter.BraceMatchingUtilAdapter;
import io.github.qeesung.brace.Brace;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.setting.HighlightBracketPairSettingsPage;
import io.github.qeesung.util.Pair;

import java.util.LinkedList;
import java.util.List;

import static io.github.qeesung.brace.BraceTokenTypes.DOUBLE_QUOTE;

/**
 * 抽象的括号高亮器，负责查找并高亮编辑器中离光标最近的成对符号。
 *
 * 目标：
 * - 防御性编程和空指针安全：所有对 editor/project/document/psiFile 等的使用均进行空值和边界检查；
 * - 边界条件与异常情况处理：校验 offset 范围、处理空文件、非 EditorEx、不可获取高亮迭代器等情况；
 * - 业务逻辑正确性：括号匹配兼容 (), {}, [] 的嵌套结构；字符串内的双引号配对优先于一般括号配对；
 *
 * 保持公共方法签名不变，仅优化内部实现与注释。
 */
abstract public class BraceHighlighter {
    public final static int NON_OFFSET = -1;
    public final static int HIGHLIGHT_LAYER_WEIGHT = 100;
    public final static BracePair EMPTY_BRACE_PAIR =
            new BracePair.BracePairBuilder().
                    leftOffset(NON_OFFSET).
                    rightOffset(NON_OFFSET).build();

    protected Editor editor;
    protected Project project;
    protected Document document;
    protected FileType fileType;
    protected CharSequence fileText;
    protected PsiFile psiFile;
    protected MarkupModelEx markupModelEx;

    /**
     * 构造函数：从 Editor 初始化所需上下文。保留签名，增加空值安全。
     *
     * @param editor 编辑器实例，不能为空
     * @throws IllegalArgumentException 当 editor 为空时抛出
     */
    public BraceHighlighter(Editor editor) {
        if (editor == null) {
            throw new IllegalArgumentException("Editor must not be null");
        }
        this.editor = editor;
        this.project = this.editor.getProject();
        this.document = this.editor.getDocument();
        PsiDocumentManager psiDocumentManager = (this.project != null) ? PsiDocumentManager.getInstance(project) : null;
        this.psiFile = (psiDocumentManager != null && this.document != null)
                ? psiDocumentManager.getPsiFile(document)
                : null;
        this.fileType = (this.psiFile != null) ? psiFile.getFileType() : null;
        this.fileText = (this.document != null) ? this.document.getImmutableCharSequence() : "";
        this.markupModelEx = (this.editor.getMarkupModel() instanceof MarkupModelEx)
                ? (MarkupModelEx) this.editor.getMarkupModel()
                : null;
    }

    /**
     * 返回当前高亮器支持的括号 token 类型对。默认返回空列表，具体由子类实现。
     *
     * @return 支持的括号元素类型对
     */
    public List<Pair<IElementType, IElementType>> getSupportedBraceToken() {
        return new LinkedList<>();
    }

    /**
     * 在支持的括号 token 中查找离给定 offset 最近的一对括号。
     * - 做好空值与边界校验；
     * - 当无法获取 EditorHighlighter 或文件信息不完整时返回 EMPTY_BRACE_PAIR。
     *
     * @param offset 光标位置
     * @return 最近的括号对；若未找到或无效则返回 EMPTY_BRACE_PAIR
     */
    public BracePair findClosetBracePairInBraceTokens(int offset) {
        if (offset < 0 || this.document == null || this.fileType == null || this.fileText == null) {
            return EMPTY_BRACE_PAIR;
        }
        int textLength = this.document.getTextLength();
        if (textLength <= 0) {
            return EMPTY_BRACE_PAIR;
        }
        boolean isBlockCaret = this.isBlockCaret();
        int safeOffset = Math.min(offset, textLength - 1);

        // 仅在 EditorEx 时才能安全获取 highlighter
        if (!(editor instanceof EditorEx)) {
            return EMPTY_BRACE_PAIR;
        }
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        if (editorHighlighter == null) {
            return EMPTY_BRACE_PAIR;
        }

        List<Pair<IElementType, IElementType>> braceTokens = this.getSupportedBraceToken();
        if (braceTokens == null || braceTokens.isEmpty()) {
            return EMPTY_BRACE_PAIR;
        }

        for (Pair<IElementType, IElementType> braceTokenPair : braceTokens) {
            if (braceTokenPair == null || braceTokenPair.getLeft() == null || braceTokenPair.getRight() == null) {
                continue;
            }
            HighlighterIterator leftTraverseIterator = editorHighlighter.createIterator(safeOffset);
            HighlighterIterator rightTraverseIterator = editorHighlighter.createIterator(safeOffset);
            int leftBraceOffset = BraceMatchingUtilAdapter.findLeftLParen(
                    leftTraverseIterator, braceTokenPair.getLeft(), this.fileText, this.fileType, isBlockCaret);
            int rightBraceOffset = BraceMatchingUtilAdapter.findRightRParen(
                    rightTraverseIterator, braceTokenPair.getRight(), this.fileText, this.fileType, isBlockCaret);
            if (leftBraceOffset != NON_OFFSET && rightBraceOffset != NON_OFFSET) {
                return new BracePair.BracePairBuilder().
                        leftType(braceTokenPair.getLeft()).
                        rightType(braceTokenPair.getRight()).
                        leftIterator(leftTraverseIterator).
                        rightIterator(rightTraverseIterator).build();
            }
        }
        return EMPTY_BRACE_PAIR;
    }

    /**
     * 在字符串 token（例如双引号包裹的字符串）内查找最近的一对引号。
     * 字符串内的引号高亮优先于普通括号。
     *
     * @param offset 光标位置
     * @return 若处于字符串 token 内且两端引号有效，返回对应的 BracePair；否则返回 EMPTY_BRACE_PAIR
     */
    public BracePair findClosetBracePairInStringSymbols(int offset) {
        if (offset < 0 || this.fileText == null || this.fileText.length() == 0 || this.document == null) {
            return EMPTY_BRACE_PAIR;
        }
        int textLength = this.document.getTextLength();
        if (textLength <= 0) {
            return EMPTY_BRACE_PAIR;
        }
        int safeOffset = Math.min(offset, textLength - 1);

        if (!(editor instanceof EditorEx)) {
            return EMPTY_BRACE_PAIR;
        }
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        if (editorHighlighter == null) {
            return EMPTY_BRACE_PAIR;
        }
        HighlighterIterator iterator = editorHighlighter.createIterator(safeOffset);
        if (iterator == null || iterator.atEnd()) {
            return EMPTY_BRACE_PAIR;
        }
        IElementType type = iterator.getTokenType();
        boolean isBlockCaret = this.isBlockCaret();
        if (type == null || !BraceMatchingUtilAdapter.isStringToken(type)) {
            return EMPTY_BRACE_PAIR;
        }

        int leftOffset = iterator.getStart();
        int rightOffset = iterator.getEnd() - 1; // 右引号位置位于字符串 token 的末尾前一个字符
        if (!isBlockCaret && leftOffset == safeOffset) {
            return EMPTY_BRACE_PAIR;
        }
        // 边界校验，避免负数/越界/逆序范围
        if (leftOffset < 0 || rightOffset < 0 || leftOffset >= textLength || rightOffset >= textLength || rightOffset < leftOffset) {
            return EMPTY_BRACE_PAIR;
        }
        return new BracePair.BracePairBuilder().
                leftType(DOUBLE_QUOTE).
                rightType(DOUBLE_QUOTE).
                leftOffset(leftOffset).
                rightOffset(rightOffset).build();
    }

    /**
     * 综合查找离 offset 最近的括号或引号配对：
     * - 若在字符串 token 内找到配对引号，则优先返回该对；
     * - 否则返回普通括号配对结果；
     * - 若均未找到，则返回 EMPTY_BRACE_PAIR。
     *
     * @param offset 光标位置
     * @return 最近的配对
     */
    public BracePair findClosetBracePair(int offset) {
        BracePair braceTokenBracePair = this.findClosetBracePairInBraceTokens(offset);
        BracePair stringSymbolBracePair = this.findClosetBracePairInStringSymbols(offset);
        if (isValidPair(braceTokenBracePair)) {
            return braceTokenBracePair;
        }
        return stringSymbolBracePair;
    }

    private static boolean isValidPair(BracePair pair) {
        if (pair == null) return false;
        Brace left = pair.getLeftBrace();
        Brace right = pair.getRightBrace();
        return left != null && right != null &&
                left.getOffset() != NON_OFFSET && right.getOffset() != NON_OFFSET;
    }

    /**
     * 根据 BracePair 高亮左右括号/引号。
     * 做好空值、边界与属性 Key 的回退处理，避免越界与 NPE。
     *
     * @param bracePair 需高亮的括号对
     * @return 左右高亮器的二元组；如果输入无效或无法高亮则返回 null
     */
    public Pair<RangeHighlighter, RangeHighlighter> highlightPair(BracePair bracePair) {
        if (bracePair == null) {
            return null;
        }
        final Brace leftBrace = bracePair.getLeftBrace();
        final Brace rightBrace = bracePair.getRightBrace();
        if (leftBrace == null || rightBrace == null) {
            return null;
        }
        final int leftBraceOffset = leftBrace.getOffset();
        final int rightBraceOffset = rightBrace.getOffset();
        final String leftBraceText = leftBrace.getText();
        final String rightBraceText = rightBrace.getText();

        if (leftBraceOffset == NON_OFFSET || rightBraceOffset == NON_OFFSET) {
            return null;
        }
        if (this.document == null) {
            return null;
        }
        int textLength = this.document.getTextLength();
        if (leftBraceOffset < 0 || rightBraceOffset < 0 || leftBraceOffset >= textLength || rightBraceOffset >= textLength) {
            return null;
        }
        if (leftBraceText == null || rightBraceText == null) {
            return null;
        }
        if (leftBraceText.length() == 0 || rightBraceText.length() == 0) {
            // 零长度的范围没有可视效果，直接忽略
            return null;
        }

        // 尝试按元素类型获取 TextAttributesKey；失败则按文本获取；仍失败则使用一个稳定的默认 Key
        TextAttributesKey textAttributesKey =
                HighlightBracketPairSettingsPage.getTextAttributesKeyByToken(leftBrace.getElementType());
        if (textAttributesKey == null) {
            textAttributesKey = HighlightBracketPairSettingsPage.getTextAttributesKeyByText(leftBraceText);
        }
        if (textAttributesKey == null) {
            textAttributesKey = HighlightBracketPairSettingsPage.PARENTHESIS_ATTR;
        }
        final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(textAttributesKey);
        // 防御：理论上不会为 null，但做回退处理避免外部环境异常
        final TextAttributes safeAttributes = (textAttributes != null) ? textAttributes : new TextAttributes();

        RangeHighlighter leftHighlighter = markupModelEx.addRangeHighlighter(
                leftBraceOffset,
                leftBraceOffset + leftBraceText.length(),
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                safeAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        RangeHighlighter rightHighlighter = markupModelEx.addRangeHighlighter(
                rightBraceOffset,
                rightBraceOffset + rightBraceText.length() + 1,
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                safeAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        return new Pair<>(leftHighlighter, rightHighlighter);
    }

    /**
     * 擦除传入列表中的全部高亮。
     *
     * @param list 需要被移除的高亮器列表，允许为 null
     */
    public void eraseHighlight(List<RangeHighlighter> list) {
        if (list == null || list.isEmpty() || this.markupModelEx == null) {
            return;
        }
        for (RangeHighlighter l : list) {
            if (l != null) {
                this.markupModelEx.removeHighlighter(l);
            }
        }
    }

    /**
     * 是否为块状光标（通常对应普通模式）。
     *
     * @return true 表示块状光标；false 表示插入模式或无法判定
     */
    public boolean isBlockCaret() {
        if (this.editor == null || this.editor.getSettings() == null) {
            return false;
        }
        return this.editor.getSettings().isBlockCursor();
    }
}
