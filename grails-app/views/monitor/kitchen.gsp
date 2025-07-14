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
    <title>Monitor Dapur</title>
    <meta http-equiv="refresh" content="5">
</head>
<body>
<h2>Pesanan Aktif di Dapur</h2>
<hr/>
<div class="row">
    <g:if test="${orders}">
        <g:each in="${orders}" var="order">
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-header fw-bold">
                        Pesanan #${order.id} - <span class="badge bg-secondary">${order.status}</span>
                    </div>
                    <ul class="list-group list-group-flush">
                        <g:each in="${order.items}" var="orderItem">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <div>
                                    ${orderItem.menuItem.name}
                                    <span class="text-muted">(x${orderItem.quantity})</span>
                                </div>
                                <span class="badge ${orderItem.status == 'PENDING' ? 'bg-danger' : 'bg-success'}">
                                    ${orderItem.status}
                                </span>
                            </li>
                        </g:each>
                    </ul>
                </div>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="col">
            <div class="alert alert-info">Dapur sedang santai, tidak ada pesanan aktif.</div>
        </div>
    </g:else>
</div>
</body>
</html>