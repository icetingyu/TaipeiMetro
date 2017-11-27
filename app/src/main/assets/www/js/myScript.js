var circleThree, circleOne, circleTwo, mTaipeiMRT, testPath, mStart, mEnd, subTouchCount, touchCount;
window.onload = function() {

  var paper = Raphael("holder", 2000, 2000);
  subTouchCount = 0;
  touchCount = 0;

  var fixedMainCircle = [];
  console.dir(paper.canvas);
  paper.canvas.onmouseup = function() {
    if (mEnd === -1 || touchCount > 2) {
      touchCount = 0;
    }
    touchCount++;
//    console.log("subTouchCount:"+subTouchCount);
//    console.log("touchCount:"+touchCount);
    if (touchCount > 2 && subTouchCount == 1) {
      for (var i = testPath.length - 1; i >= 0; i--) {
        var s = testPath.pop();
        if( s.getData().obj.customID === "T207" ||
            s.getData().obj.customID === "T412" ||
            s.getData().obj.customID === "T24"  ||
            s.getData().obj.customID === "T23"  ||
            s.getData().obj.customID === "T43"  ||
            s.getData().obj.customID === "T13"  ||
            s.getData().obj.customID === "T35"  ||
            s.getData().obj.customID === "T25"  ||
            s.getData().obj.customID === "T45"  ||
            s.getData().obj.customID === "T15"  ||
            s.getData().obj.customID === "T51"  ||
            s.getData().obj.customID === "T32"  ||
            s.getData().obj.customID === "T42"  ||
            s.getData().obj.customID === "T12"  ||
            s.getData().obj.customID === "T34"  ||
            s.getData().obj.customID === "T303"
          ){
          s.animate({
            fill: "#000"
          }, 100);
        }else{
          s.animate({
            fill: "#FFF"
          }, 100);
        }
      }
      mStart = -1;
      mEnd = -1;
      var json = '{"StartChs":\"'+null+'\","StartEn":\"'+null+'\","EndChs":\"'+null+'\","EndEn":\"'+null+'\"}';
	  window.JSInterface.runOnAndroidJavaScript(json);//调用android的函数
    }
  };

  var config = new Config();
  config.setRoadLength(65);
  config.setRoadWidth(14, 6);
  config.setCircleSize(13, 11, 28);
  config.setFontSize(14, 10, 8);
  config.setCircleWidth(2, 2, 0);

  mStart = -1;
  mEnd = -1;
  testPath = [];

  var objLength = Obj.length,
    ObjPathLength = ObjPath.length,
    i, j;

  mTaipeiMRT = new TaipeiMRT(ObjMatrix, Obj);

  for (i = ObjPathLength - 1; i >= 0; i--) {
    var one = ObjPath[i];
    var oneLength = one.path.length;
    var mPath = [];
    for (j = 0; j < oneLength; j++) {
      if (one.path[j].direction.length === 1) {
        mPath = [
          ["M", config.roadLength * one.row, config.roadLength * one.col],
          ["L", config.roadLength * one.path[j].row, config.roadLength * one.path[j].col]
        ];
        paper.path(mPath).attr({
          stroke: ObjLine[one.path[j].direction].color,
          "stroke-width": config.roadWidth,
          "stroke-linecap": "round"
        });
      } else {
        mPath = [
          ["M", config.roadLength * one.row, config.roadLength * one.col],
          ["L", config.roadLength * one.path[j].row, config.roadLength * one.path[j].col]
        ];
        paper.path(mPath).attr({
          stroke: ObjLine[one.path[j].direction[0]].color,
          "stroke-width": config.roadWidth,
          "stroke-linecap": "round"
        });
        mPath = [
          ["M", config.roadLength * one.row, config.roadLength * one.col],
          ["L", config.roadLength * one.path[j].row, config.roadLength * one.path[j].col]
        ];
        // paper.path(mPath).attr({
        //     stroke: "#FFF",
        //     "stroke-width": config.roadTwoWidth,
        //     "stroke-linecap": "round"
        // });
      }
    }
  }

  circleThree = paper.set();
  circleOne = paper.set();
  circleTwo = paper.set();

  var mDrawFonts = new DrawFonts("#000", "Arial, Helvetica, sans-serif", config.fontSizeTw, config.fontSizeEn, paper);
  for (i = 0; i < objLength; i++) {
    switch (Obj[i].txtDirection) {
      case 1:
        mDrawFonts.textAnchor = "start";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 0.8);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 3.0);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 0.8);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 1.5);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 2:
        mDrawFonts.textAnchor = "start";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 1.5);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 0.75);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 1.5);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 0.75);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 3:
        mDrawFonts.textAnchor = "start";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 0.8);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 1.8);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) + (config.circleOneSize * 0.8);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 3.3);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 4:
        mDrawFonts.textAnchor = "middle";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 2.0);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 3.5);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 5:
        mDrawFonts.textAnchor = "end";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 0.8);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 1.8);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 0.8);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 3.3);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 6:
        mDrawFonts.textAnchor = "end";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 1.5);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 0.75);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 1.5);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) + (config.circleOneSize * 0.75);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 7:
        mDrawFonts.textAnchor = "end";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 0.8);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 3.0);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row) - (config.circleOneSize * 0.8);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 1.5);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
      case 8:
        mDrawFonts.textAnchor = "middle";
        mDrawFonts.fontTwX = (config.roadLength * Obj[i].row);
        mDrawFonts.fontTwY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 3.5);
        mDrawFonts.fontEnX = (config.roadLength * Obj[i].row);
        mDrawFonts.fontEnY = (config.roadLength * Obj[i].col) - (config.circleOneSize * 2.0);
        mDrawFonts.fontTW = Obj[i].nameTW;
        mDrawFonts.fontEN = Obj[i].nameEN;
        mDrawFonts.drawFont();
        break;
    }

    circleOne.push(
      paper.circle(config.roadLength * Obj[i].row, config.roadLength * Obj[i].col, config.circleOneSize)
      .attr({
        stroke: "#000",
        "stroke-width": config.circleOneWidth
      })
      .data("obj", Obj[i])
    );

    var circleTwoTmp;
    if (Obj[i].customID === "T207" ||
      Obj[i].customID === "T412" ||
      Obj[i].customID === "T24" ||
      Obj[i].customID === "T23" ||
      Obj[i].customID === "T43" ||
      Obj[i].customID === "T13" ||
      Obj[i].customID === "T35" ||
      Obj[i].customID === "T25" ||
      Obj[i].customID === "T45" ||
      Obj[i].customID === "T15" ||
      Obj[i].customID === "T51" ||
      Obj[i].customID === "T32" ||
      Obj[i].customID === "T42" ||
      Obj[i].customID === "T12" ||
      Obj[i].customID === "T34" ||
      Obj[i].customID === "T303") {
      circleTwoTmp = paper.circle(config.roadLength * Obj[i].row, config.roadLength * Obj[i].col, config.circleTwoSize)
        .attr({
          fill: "#000",
          stroke: "#FFF",
          "stroke-width": config.circleTwoWidth
        })
        .data("obj", Obj[i]);

    } else {
      circleTwoTmp = paper.circle(config.roadLength * Obj[i].row, config.roadLength * Obj[i].col, config.circleTwoSize)
        .attr({
          fill: "#FFF",
          stroke: "#FFF",
          "stroke-width": config.circleTwoWidth
        })
        .data("obj", Obj[i]);
    }
    circleTwo.push(circleTwoTmp);

    /**
     * 設定車站編號
     */
    if (Obj[i].customID === "T207" ||
      Obj[i].customID === "T412" ||
      Obj[i].customID === "T24" ||
      Obj[i].customID === "T23" ||
      Obj[i].customID === "T43" ||
      Obj[i].customID === "T13" ||
      Obj[i].customID === "T35" ||
      Obj[i].customID === "T25" ||
      Obj[i].customID === "T45" ||
      Obj[i].customID === "T15" ||
      Obj[i].customID === "T51" ||
      Obj[i].customID === "T32" ||
      Obj[i].customID === "T42" ||
      Obj[i].customID === "T12" ||
      Obj[i].customID === "T34" ||
      Obj[i].customID === "T303") {
      paper.text(
        config.roadLength * Obj[i].row,
        config.roadLength * Obj[i].col,
        Obj[i].customID
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId
      });
    } else {
      paper.text(
        config.roadLength * Obj[i].row,
        config.roadLength * Obj[i].col,
        Obj[i].customID
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId
      });
    }

    /**
     * 設定車站特殊編號[硬幹法]
     */
    if (Obj[i].customID === "T412") {
      paper.text(
        config.roadLength * Obj[i].row + (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "412"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T24") {
      paper.text(
        config.roadLength * Obj[i].row + (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "411"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col - (config.circleOneSize) * 1.5,
        "216"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T23") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col - (config.circleOneSize) * 1.5,
        "218"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "314"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });
    }

    if (Obj[i].customID === "T43") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "315"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "408"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T13") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "316"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "111"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T35") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 0.75,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.2,
        "510"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "312"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T25") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.5,
        config.roadLength * Obj[i].col,
        "511"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col - (config.circleOneSize) * 1.5,
        "219"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T45") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "513"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "407"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T15") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "514"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "110"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T51") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "522"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col - (config.circleOneSize) * 1.5,
        "124"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T12") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "224"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "109"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T42") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 0.75,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.2,
        "406"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 1.2,
        config.roadLength * Obj[i].col,
        "222"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });
    }

    if (Obj[i].customID === "T32") {
      paper.text(
        config.roadLength * Obj[i].row + (config.circleOneSize) * 0.75,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.2,
        "310"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col - (config.circleOneSize) * 1.5,
        "221"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    if (Obj[i].customID === "T34") {
      paper.text(
        config.roadLength * Obj[i].row + (config.circleOneSize) * 0.75,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.2,
        "309"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });

      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) * 0.75,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.2,
        "405"
      ).attr({
        "fill": "#000",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "end"
      });
    }

    if (Obj[i].customID === "T303") {
      paper.text(
        config.roadLength * Obj[i].row - (config.circleOneSize) / 2,
        config.roadLength * Obj[i].col + (config.circleOneSize) * 1.5,
        "303"
      ).attr({
        "fill": "#FFF",
        "font-family": "Arial, Helvetica, sans-serif",
        "font-size": config.fontSizeId,
        'text-anchor': "start"
      });
    }

    circleThree.push(
      paper.circle(config.roadLength * Obj[i].row, config.roadLength * Obj[i].col, config.circleThreeSize)
      .attr({
        fill: "#FFF",
        "fill-opacity": 0,
        "stroke-opacity": 0,
        "stroke": "#2ECCFA",
        "stroke-width": config.circleTwoWidth
        // "stroke-width": config.circleThreeWidth
      })
      .data("obj", Obj[i])
    );

    circleThree[i].touchend(function() {
      if (mStart === -1) {
        subTouchCount = 0;
        mStart = this.getData().obj.id;
        mTaipeiMRT.setStartStation(mStart);
        circleTwo[mStart].animate({
          fill: "#2ECCFA"
        }, 100);
        testPath.push(circleTwo[mStart]);
        mStart = this.getData().obj.id;  
                startStationTw = mTaipeiMRT.getTaipeiMetroNameTw(mStart);
                startStationEn = mTaipeiMRT.getTaipeiMetroNameEn(mStart);     
                var json = '{"StartChs":\"'+startStationTw+'\","StartEn":\"'+startStationEn+'\","EndChs":\"'+null+'\","EndEn":\"'+null+'\"}';
				window.JSInterface.runOnAndroidJavaScript(json);//调用android的函数
		//window.JSInterface.alertMessage(mTaipeiMRT.getTaipeiMetroGeo(mStart));
		mTaipeiMRT.setStartStation(mStart);
      } else if (mEnd === -1) {
        if (mStart === this.getData().obj.id) {
          return;
        }
        subTouchCount++;
        mEnd = this.getData().obj.id;
        circleTwo[mEnd].animate({
          fill: "#2ECCFA"
        }, 100);
        testPath.push(circleTwo[mEnd]);
        mEnd = this.getData().obj.id;
                endStationTw = mTaipeiMRT.getTaipeiMetroNameTw(mEnd);
                endStationEn = mTaipeiMRT.getTaipeiMetroNameEn(mEnd);     
                var json = '{"StartChs":\"'+startStationTw+'\","StartEn":\"'+startStationEn+'\","EndChs":\"'+endStationTw+'\","EndEn":\"'+endStationEn+'\"}';
                //alert(json);
                //window.JSInterface.alertMessage(getTaipeiMetroGeo(mEnd));
		window.JSInterface.runOnAndroidJavaScript(json);//调用android的函数
		// window.JSInterface.alertMessage(mTaipeiMRT.getTaipeiMetroGeo(mEnd));

        mTaipeiMRT.setEndStation(mEnd);
        var allPath = mTaipeiMRT.getPathPlanning();
		
        console.dir(allPath);

//        var allPathLength = allPath.path.length;
//        for (var j = 1; j < allPathLength - 1; j++) {
//          var circleAnimate = circleTwo[allPath.path[j]];
//          circleAnimate.animate({
//            fill: "#2ECCFA"
//          }, 100);
//          testPath.push(circleAnimate);
//        }

        /**
         * 目前所有路線規劃後的資料存在 allPath 中
         * allPath.mapping : 整條路線規劃後，對應到地圖上的 ID 列表
         */
      }
    });
  }
};

/**
 * 投入的車站 ID 標示出指定車站
 */
function markNearStation(_customID){
	var _stationID = convertCustomIDtoStationID(_customID);

  for(var i=circleThree.length-1; i>=0; i-- ){
    circleThree[i].animate({
      "stroke-opacity": 0
    }, 100);
  }

  circleThree[_stationID].animate({
    "stroke-opacity": 1
  }, 100);
}

/**
 * 投入的車站 ID 標示出指定車站並設定為起始站
 */
function markAndStartNearStation(_customID){
  var _stationID = convertCustomIDtoStationID(_customID);
  console.log("_customID : "+_customID);
  console.log("_stationID : "+_stationID);
  for(var i=circleThree.length-1; i>=0; i-- ){
    circleThree[i].animate({
      "stroke-opacity": 0
    }, 100);
  }

  circleThree[_stationID].animate({
    "stroke-opacity": 1
  }, 100);

  subTouchCount = 0;
  touchCount=1;
  mStart = _stationID;
  mTaipeiMRT.setStartStation(mStart);
  circleTwo[mStart].animate({
    fill: "#2ECCFA"
  }, 100);
  testPath.push(circleTwo[mStart]);
}
function convertCustomIDtoStationID(_customID){
  for(var i=circleThree.length-1; i>=0; i-- ){
  	// console.log("circleThree[i].getData().obj.customID :"+circleThree[i].getData().obj.customID);
    if(circleThree[i].getData().obj.customID === _customID){
      return i;
    }
  }
  return -1;
}

