function Config() {
    this.roadLength = 0;
    this.roadWidth = 0;
    this.roadTwoWidth = 0;
    this.circleOneSize = 0;
    this.circleTwoSize = 0;
    this.circleThreeSize = 0;
    this.circleOneWidth = 0;
    this.circleTwoWidth = 0;
    this.circleThreeWidth = 0;
    this.fontSizeTw = 0;
    this.fontSizeEn = 0;
    this.fontSizeId = 0;
}
Config.prototype = {
    setRoadLength: function(_roadLength) {
        this.roadLength = _roadLength;
    },
    setRoadWidth: function(_roadWidth, _roadTwoWidth) {
        this.roadWidth = _roadWidth;
        this.roadTwoWidth = _roadTwoWidth;
    },
    setCircleSize: function(_oneSize, _twoSize, _threeSize) {
        this.circleOneSize = _oneSize;
        this.circleTwoSize = _twoSize;
        this.circleThreeSize = _threeSize;
    },
    setFontSize: function(_sizeTw, _sizeEn, _sizeId) {
        this.fontSizeTw = _sizeTw;
        this.fontSizeEn = _sizeEn;
        this.fontSizeId = _sizeId;
    },
    setCircleWidth: function(_oneWidth, _twoWidth, _threeWidth) {
        this.circleOneWidth = _oneWidth;
        this.circleTwoWidth = _twoWidth;
        this.circleThreeWidth = _threeWidth;
    }
};
