from werkzeug.local import LocalProxy
import ujson

from myfecviz import get_db

db = LocalProxy(get_db)


def get_number_of_candidates():
    """Return the number of candidates registered with the FEC.

    This serves as just an example query.

    :returns: integer
    """
    # Execute database query
    db.execute("SELECT COUNT(*) FROM candidates;")
    results = db.fetchall()

    # Package into output
    return int(results[0][0])


def get_all_transaction_amounts():
    """Return all transaction amounts with the state that the contribution came from.

    For all committee contributions with a transaction_amt greater than zero,
    return every transaction amount with the state that the contribution came form.

    :return: List of dictionaries with 'state' and 'amount' keys
    """
    
    # Execute database query
    db.execute("SELECT state, transaction_amt FROM committee_contributions WHERE transaction_amt > 0;")
    results = db.fetchall()

    list_amt_by_state = []
    for pairs in results:
        dict_temp = {'state':pairs[0], 'amount':float(pairs[1])}
        list_amt_by_state.append(dict_temp)

    # Package into output
    return list_amt_by_state


def get_total_transaction_amounts_by_state():
    """Return a list of dicts containing the state and total contributions.

    For all committee contributions with a transaction_amt greater than zero,
    return a dictionary containing the state and total amount.

    (From README: For every state, return a dictionary containing the state
    and total amount of non-zero contributions from the committee contributions table.)

    :returns: List of dictionaries with 'state' and 'total_amount' keys
    """
    
    # Execute database query
    db.execute("SELECT state, SUM(transaction_amt) FROM committee_contributions WHERE transaction_amt > 0 GROUP BY state;")
    results = db.fetchall()

    list_state_amt_total = []
    for pairs in results:
        dict_temp = {'state':pairs[0], 'total_amount':float(pairs[1])}
        list_state_amt_total.append(dict_temp)

    # Package into list of dictionaries
    return list_state_amt_total
