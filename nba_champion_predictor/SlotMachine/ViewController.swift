//
//  ViewController.swift
//  SlotMachine
//
//  Created by David Luong on 12/4/17.
//  Copyright © 2017 David Luong. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    /**
        7 outlets used, excluding the header image (banner) and title label (machineLabel), and an additional, 8th outlet for the group component (winnerContainer) that contains the winnerLabel and winnerImage.
    */
    @IBOutlet weak var token1: UIImageView!
    @IBOutlet weak var token2: UIImageView!
    @IBOutlet weak var token3: UIImageView!
    @IBOutlet weak var winnerContainer: UIView!
    @IBOutlet weak var winnerLabel: UILabel!
    @IBOutlet weak var winnerImage: UIImageView!
    @IBOutlet weak var playButton: UIButton!
    @IBOutlet weak var resetButton: UIButton!
    
    //  Fixed array for 8/30 NBA teams, and a empty string as a placeholder for index 0.
    let slotOptions = ["", "img1.png", "img2.png", "img3.png", "img4.png", "img5.png", "img6.png", "img7.png", "img8.png"]
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Hide the winner label and reset button when there's no jackpot
        self.winnerContainer.isHidden = true
        self.winnerLabel.isHidden = true
        self.winnerImage.isHidden = true
        self.resetButton.isHidden = true

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning() // Dispose of any resources that can be recreated.
    }
    
    //Reset button and its functionality
    @IBAction func resetBtn(_ sender: UIButton) {
        //reset the screen to display the same as the start screen.
        self.token1.image = UIImage(named: "img1.png")  // set default slot to 2017's NBA Champion
        self.token2.image = UIImage(named: "img1.png")  // set default slot to 2017's NBA Champion
        self.token3.image = UIImage(named: "img1.png")  // set default slot to 2017's NBA Champion
        
        self.winnerContainer.isHidden = true  // hide the winnerContainer at the start of game
        self.winnerLabel.isHidden = true      // hide the winnerLabel at the start of game
        self.winnerImage.isHidden = true      // hide the winnerImage at the start of game
        self.resetButton.isHidden = true      // hide the resetButton at the start of game
        self.playButton.isHidden = false   // show the playButton at the start of game, but hide resetButton
    }
    
    //Play button and its functionality
    @IBAction func playBtn(_ sender: UIButton) {
        
        //  generate and store 3 random #'s to be used as array indices.
        let num1 = randomNumber(range: slotOptions.index(of: "img1.png")!...slotOptions.index(of: "img8.png")!)
        let num2 = randomNumber(range: slotOptions.index(of: "img1.png")!...slotOptions.index(of: "img8.png")!)
        let num3 = randomNumber(range: slotOptions.index(of: "img1.png")!...slotOptions.index(of: "img8.png")!)
        
        self.token1.image = UIImage(named: "img\(num1).png")
        self.token2.image = UIImage(named: "img\(num2).png")
        self.token3.image = UIImage(named: "img\(num3).png")
        
        //  If all three images are the same, hide/show properties that are necessary
        if( ((self.token2.image?.isEqual(self.token1.image))! && (self.token3.image?.isEqual(self.token1.image))!) || ((self.token1.image?.isEqual(self.token2.image))! && (self.token3.image?.isEqual(self.token2.image))!) || ((self.token1.image?.isEqual(self.token3.image))! && (self.token2.image?.isEqual(self.token3.image))!)){
        
            self.winnerContainer.isHidden = false   // show the winnerContainer for every win
            self.winnerLabel.isHidden = false       // show the winnerLabel if winning
            self.winnerImage.isHidden = false       // show the winnerImage if winning
            self.resetButton.isHidden = false       // show the resetButton if winning
            self.playButton.isHidden = true         // hide the playButton if winning, but show resetButton
        }
    }
    
    //Get a random number between 1 and 8
    func randomNumber(range: ClosedRange<Int> = 1...8) -> Int {
        let min = range.lowerBound  //lowerBound replaces 'startIndex' in the latest Swift version
        let max = range.upperBound  //upperBound replaces 'endIndex' in the latest Swift version
        
        return Int(arc4random_uniform(UInt32(max-min))) + min
    }
    
}
