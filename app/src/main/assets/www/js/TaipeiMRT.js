function TaipeiMRT(_objMatrix, _objStations) {
    this.objMatrix = _objMatrix;
    this.objStations = _objStations;
    this.stationsNumber = this.objStations.length;
    this.maxDistance = 999999;
    this.startStation = -1;
    this.endStation = -1;
    this.objNameTw = [];
    this.objNameEn = [];
    this.allDistance = 0;
    for (var i = 0; i < this.stationsNumber; i++) {
        this.objNameTw.push(this.objStations[i].nameTW);
        this.objNameEn.push(this.objStations[i].nameEN);
    }
}

TaipeiMRT.prototype = {
    getIndexFromTaipeiMetroNameTw: function(_nameTw) {
        return this.objNameTw.indexOf(_nameTw);
    },
    getIndexFromTaipeiMetroNameEn: function(_nameEn) {
        return this.objNameEn.indexOf(_nameEn);
    },
    getTaipeiMetroNameTw: function(_index) {
        return this.objStations[_index].nameTW;
    },
    getTaipeiMetroNameEn: function(_index) {
        return this.objStations[_index].nameEN;
    },
    setStartStation: function(_start) {
        this.startStation = _start;
    },
    setEndStation: function(_end) {
        this.endStation = _end;
    },
    getPathPlanning: function() {
        var Distance = new Array(this.stationsNumber);
        var prev = new Array(this.stationsNumber);
        var s = new Array(this.stationsNumber);
        var mindis = 0,
            dis = 0,
            i = 0,
            j = 0,
            u = 0;
        for (i = 0; i < this.stationsNumber; i++) {
            Distance[i] = this.objMatrix[this.startStation][i];
            s[i] = 0;
            if (Distance[i] == this.maxDistance) {
                prev[i] = -1;
            } else {
                prev[i] = this.startStation;
            }
        }
        Distance[this.startStation] = 0;
        s[this.startStation] = 1;
        for (i = 1; i < this.stationsNumber; i++) {
            mindis = this.maxDistance;
            u = this.startStation;
            for (j = 0; j < this.stationsNumber; j++) {
                if (s[j] === 0 && Distance[j] < mindis) {
                    mindis = Distance[j];
                    u = j;
                }
            }
            s[u] = 1;
            for (j = 0; j < this.stationsNumber; j++) {
                if (s[j] === 0 && this.objMatrix[u][j] < this.maxDistance) {
                    dis = Distance[u] + this.objMatrix[u][j];
                    if (Distance[j] > dis) {
                        Distance[j] = dis;
                        prev[j] = u;
                    }
                }
            }
        }

        var sortArray = [];
        var finalStation = this.endStation;
        do {
            sortArray.unshift(finalStation);
            finalStation = prev[finalStation];
        } while (finalStation !== this.startStation);
        sortArray.unshift(this.startStation);
        var sortArrayLength = sortArray.length;

        this.allDistance = 0;
        var temp = [];
        var transferStation = [];
        for (i = 1; i < sortArrayLength; i++) {
            var tmp = this.objStations[sortArray[(i - 1)]].detail.length;
            for (j = 0; j < tmp; j++) {
                if (this.objStations[sortArray[(i - 1)]].detail[j].id === sortArray[i]) {
                    if (temp.length > this.objStations[sortArray[(i - 1)]].detail[j].direction.length || temp.toString() !== this.objStations[sortArray[(i - 1)]].detail[j].direction.toString()) {
                        var tmpStation = this.objStations[sortArray[i-1]].id;
                        var tmpDirection = this.objStations[sortArray[i-1]].detail[j].direction;
                        transferStation.push({id:tmpStation,direction:tmpDirection});
                        temp = this.objStations[sortArray[i-1]].detail[j].direction;
                    }
                    this.allDistance = this.allDistance + this.objStations[sortArray[(i - 1)]].detail[j].distance;
                    break;
                }
            }
        }

        var mappingArray = [];
        for(k = 0; k<sortArray.length; k++){
          mappingArray.push(this.objStations[sortArray[k]].customID);
        }
        var all = {
            transferStation: transferStation,
            startStation: this.startStation,
            endStation: this.endStation,
            allDistance: this.allDistance,
            path: sortArray,
            mapping: mappingArray
        };
        return all;
    }
};
