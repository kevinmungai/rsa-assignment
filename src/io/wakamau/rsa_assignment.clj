(ns io.wakamau.rsa-assignment)

(defn gen-primes
  "Generates an infinite, lazy sequence of prime numbers"
  []
  (let [reinsert (fn [table x prime]
                   (update-in table [(+ prime x)] conj prime))]
    (defn primes-step [table d]
      (if-let [factors (get table d)]
        (recur (reduce #(reinsert %1 d %2) (dissoc table d) factors)
               (inc d))
        (lazy-seq (cons d (primes-step (assoc table (* d d) (list d))
                                       (inc d))))))
    (primes-step {} 2)))

(defn p-and-q
  []
  (let [p (rand-nth (take 100 (gen-primes)))
        q (rand-nth (take 100 (gen-primes)))]
    {:p p
     :q q}))

(defn gcd 
  "(gcd a b) computes the greatest common divisor of a and b.
  using \"euclid's\" algorithm "
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))


(defn from-one-to-phi [to]
  (filter #(= 1 (gcd to %)) (range 2 to)))

(defn find-d
  [e phi]
  (loop [d 0 n false]
    (if (integer? n)
      (dec d)
      (recur (inc d)
             (/ (dec (* e d)) phi)))))





