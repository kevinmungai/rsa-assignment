# io.wakamau/rsa-assignment

## generate prime numbers

```clojure
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
```


the above code uses `lazy-seq` to create an infinite series of primes numbers.

## generate `p` and `q`

lets's say that we want `100` prime numbers

```clojure
dev=> (rand-nth (take 1000 (gen-primes)))
631
```

```clojure
(defn p-and-q
  []
  (let [p (rand-nth (take 50 (gen-primes)))
        q (rand-nth (take 50 (gen-primes)))]
    {:p (bigint p)
     :q (bigint q)}))


dev=> (p-and-q)
{:p 13N :q 53N}
```

## get "n"

```clojure
dev=> (def p-q (p-and-q))
#'dev/p-q

dev=> (p-q)
{:p 13N :q 53N}

dev=> (def p (:p (p-q)))
#'dev/p

dev=> p
13N

dev=> (def q (:q (p-q)))
#'dev/q

dev=> q
53N

dev=> (def n (*' p q)
#'dev/n

dev=> n
689N
```

## find phi

phi is defined as `(p - 1) * (q - 1)`
```clojure
dev=> (def phi (*' (p - 1) (q - 1)))
#'dev/phi

dev=> phi
624N
```

## select integer "e"

"e" must fall between 1 and `phi` and must also have a gcd of 1 between it and `phi`

```clojure
(defn gcd 
  "(gcd a b) computes the greatest common divisor of a and b.
  using \"euclid's\" algorithm "
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

(defn from-one-to-phi [to]
  (filter #(= 1 (gcd to %)) (range 2 to)))
```

we are now able to find all the numbers between 1 and `phi` that have a gcd of 1 with `phi` as well

```clojure
dev=> (from-one-to-phi 624N)

(5 7 11 17 19 23.......
```

the `e` we've chosen is `7`

## calculate "d"

next thing we want to calculate `d`

`d` if multiplied by `e` and the result minused by `1` and then divided by `phi` should give a whole number

```clojure
(defn find-d
  [e phi]
  (loop [d 0 n false]
    (if (integer? n)
      (dec d)
      (recur (inc d)
             (/ (dec (*' e d)) phi)))))
```

```clojure
dev=> (find-d 7 624N)
535
```

d = `535`

## publish public information

Now we can publish a public key 

`PU = {e, n}`
`PU = {7, 689N}`

## the private information is:

`PR = {d, n}`
`PR = {535, 689N}`


## encrypt a message

let's define a message as `33`

```clojure
dev=> (def message 33)
```

to encrypt a message we use the exponential of the public key and then we `mod` that with the `n`

```clojure
dev=> (mod (Math/pow message (bigint 7)) 689N)
32.0

dev=> (def cipher-text (mod (Math/pow message (bigint 7) 689N)))
#'dev/cipher-text
```


## decrypt message

```clojure
dev=> (mod (Math/pow cipher-text 535N) 689N)
32.0
```
