<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
  ~ Project: WATCHME Consortium, http://www.project-watchme.eu/
  ~ Creator: University of Reading, UK, http://www.reading.ac.uk/
  ~ Creator: NetRom Software, RO, http://www.netromsoftware.ro/
  ~ Contributor: $author, $affiliation, $country, $website
  ~ Version: 0.1
  ~ Date: 31/7/2015
  ~ Copyright: Copyright (C) 2014-2017 WATCHME Consortium
  ~ License: The MIT License (MIT)
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~ The above copyright notice and this permission notice shall be included in all
  ~ copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  ~ SOFTWARE.
  --%>

<!DOCTYPE html>
<html>
<head>
    <title>Student Model API</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>
<style type="text/css">
    pre {
        outline: 1px solid #ccc;
        padding: 5px;
        margin: 5px;
    }

    .string {
        color: green;
    }

    .number {
        color: darkorange;
    }

    .boolean {
        color: blue;
    }

    .null {
        color: magenta;
    }

    .key {
        color: red;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        var requestJson = '${it.getRequestModelAsJson()}';
        var responseType1Json = '${it.getResponseModelType1AsJson()}';
        var responseType2Json = '${it.getResponseModelType2AsJson()}';

        requestJson = JSON.stringify(JSON.parse(requestJson), null, 2);
        responseType1Json = JSON.stringify(JSON.parse(responseType1Json), null, 2);
        responseType2Json = JSON.stringify(JSON.parse(responseType2Json), null, 2);

        $('#request-panel').html(syntaxHighlight(requestJson));
        $('#response-panel-1').html(syntaxHighlight(responseType1Json));
        $('#response-panel-2').html(syntaxHighlight(responseType2Json));
    });

    function syntaxHighlight(json) {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    }
</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">JIT request format</h3>
    </div>
    <div class="panel-body">
            <pre id="request-panel">
            </pre>
    </div>
</div>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">JIT response format for:
            <ul>
                <li>Scenario 1: Query all EPAs for a given student to receive level 1 feedback</li>
                <li>Scenario 2: Query one EPA, to receive all the feedback types for the selected EPA (Level 1 + Level
                    2)
                </li>
            </ul>
        </h3>
    </div>
    <div class="panel-body">
            <pre id="response-panel-1">
            </pre>
    </div>
</div>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">JIT response format for:
            <ul>
                <li>Scenario 3: Query one EPA, one message type to receive level 2 feedback</li>
            </ul>
        </h3>
    </div>
    <div class="panel-body">
            <pre id="response-panel-2">
            </pre>
    </div>
</div>
</body>
</html>