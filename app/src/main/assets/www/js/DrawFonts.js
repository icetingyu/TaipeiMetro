function DrawFonts(_fontColor, _fonts, _fontSizeTW, _fontSizeEN, _paper) {
    this.fontColor = _fontColor;
    this.fonts = _fonts;
    this.fontSizeTW = _fontSizeTW;
    this.fontSizeEN = _fontSizeEN;
    this.paper = _paper;
    this.fontTwX = 0;
    this.fontTwY = 0;
    this.fontEnX = 0;
    this.fontEnY = 0;
    this.fontTW = "";
    this.fontEN = "";
    this.textAnchor = "";
}
DrawFonts.prototype.drawFont = function() {
    this.paper.text(this.fontTwX, this.fontTwY, this.fontTW).attr({
        fill: this.fontColor,
        "font-family": this.fonts,
        "font-size": this.fontSizeTW,
        'text-anchor': this.textAnchor
    });
    this.paper.text(this.fontEnX, this.fontEnY, this.fontEN).attr({
        fill: this.fontColor,
        "font-family": this.fonts,
        "font-size": this.fontSizeEN,
        'text-anchor': this.textAnchor
    });
};
