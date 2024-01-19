let stompClient = null;

var strMatrix = "";

var show = false;

const setConnected = (connected) => {

    const calculateBtn = document.getElementById("calculate");
    const disconnectBtn = document.getElementById("disconnect");

    calculateBtn.disabled = connected;
    disconnectBtn.disabled = !connected;

    const resultTable = document.getElementById("resultTable");
    resultTable.hidden = !connected;
}

const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

const calculate = () => {
    stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
    stompClient.connect({}, (frame) => {
        setConnected(true);

        const a = [];
        const b = [];

        for (var i = 1; i <= document.f1.N_select.value; i++) {
            var n = [];
            for (var j = 1; j <= document.f1.N_select.value; j++) {
                var el = parseFloat(document.getElementById("c"+i+""+j).value);
                if (isNaN(el)) {
                    el = 0;
                }
                n.push(el);
            }
            a.push(n.join(","));
       }

       var size_matrix = parseFloat(document.f1.N_select.value) + 1;
       console.log(`size_matrix: ${size_matrix}`);
       for (var j = 1; j <= document.f1.N_select.value; j++) {
            var el = parseFloat(document.getElementById("c"+j+""+size_matrix).value);
            if (isNaN(el)) {
                el = 0;
            }
            b.push(el);
       }

       console.log(`matrix: ${a}`);
       console.log(`vector: ${b}`);

       a.push(b.join(","));

       strMatrix = a.join("|");

       console.log(`Connected to strMatrix: ${strMatrix} frame:${frame}`);
       stompClient.subscribe(`/topic/response.${strMatrix}`, (matrix) => showMatrixDetail(JSON.parse(matrix.body)));
    });
}

const showMatrixDetail = (matrix) => {
    show = true;

    document.getElementById("det").innerHTML = matrix.det;
    document.getElementById("reverse").innerHTML = matrix.reverse.replaceAll("][", "<br/>").replaceAll(",", "<span>_____</span>").replaceAll("[", "").replace("]", "");
    document.getElementById("transpose").innerHTML = matrix.transpose.replaceAll("][", "<br/>").replaceAll(",", "<span>_____</span>").replaceAll("[", "").replace("]", "");
    document.getElementById("decomposition").innerHTML = matrix.decomposition.replaceAll(",", "<br/>");
}

const getMatrixDetails = () => {
    if (show == false) {
        if (strMatrix !== "") {
            stompClient.send(`/app/matrix.${strMatrix}`);
        } else {
            alert("values in the table cells is empty");
        }
    } else {
        alert("matrix detail is show");
    }
}