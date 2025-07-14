<%--
  Created by IntelliJ IDEA.
  User: ilal
  Date: 13/07/25
  Time: 23.10
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="warkop"/>
    <title>Monitor Meja Saji</title>
    <meta http-equiv="refresh" content="5">
</head>
<body>
<h2>Pesanan Siap Diantar</h2>
<hr/>
<div class="row">
    <g:if test="${orders}">
        <g:each in="${orders}" var="order">
            <div class="col-md-4 mb-4">
                <div class="card border-success h-100">
                    <div class="card-header bg-success text-white fw-bold">
                        Pesanan #${order.id} - SIAP!
                    </div>
                    <ul class="list-group list-group-flush">
                        <g:each in="${order.items}" var="orderItem">
                            <li class="list-group-item">
                                ${orderItem.menuItem.name} (x${orderItem.quantity})
                            </li>
                        </g:each>
                    </ul>
                </div>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="col">
            <div class="alert alert-secondary">Tidak ada pesanan yang siap diantar.</div>
        </div>
    </g:else>
</div>
</body>
</html>