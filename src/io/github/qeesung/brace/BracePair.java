package io.github.qeesung.brace;

import com.intellij.psi.tree.IElementType;

public class BracePair {
    private final Brace leftBrace;
    private final Brace rightBrace;

    public BracePair(IElementType leftType,
                     IElementType rightType,
                     String leftText,
                     String rightText,
                     int leftOffset,
                     int rightOffset) {
        this.leftBrace = new Brace(leftType, leftText, leftOffset);
        this.rightBrace = new Brace(rightType, rightText, rightOffset);
    }

    public Brace getLeftBrace() {
        return leftBrace;
    }

    public Brace getRightBrace() {
        return rightBrace;
    }

    public static class BracePairBuilder {
        private IElementType leftType;
        private IElementType rightType;
        private int leftOffset;
        private int rightOffset;

        public BracePairBuilder leftType(IElementType type) {
            this.leftType = type;
            return this;
        }

        public BracePairBuilder rightType(IElementType type) {
            this.rightType = type;
            return this;
        }

        public BracePairBuilder leftOffset(int offset) {
            this.leftOffset = offset;
            return this;
        }

        public BracePairBuilder rightOffset(int offset) {
            this.rightOffset = offset;
            return this;
        }

        public BracePair build() {
            String leftText = BraceTokenType.getElementTypeText(this.leftType);
            String rightText = BraceTokenType.getElementTypeText(this.rightType);
            return new BracePair(this.leftType, this.rightType,
                    leftText, rightText,
                    this.leftOffset, this.rightOffset);
        }
    }
}
